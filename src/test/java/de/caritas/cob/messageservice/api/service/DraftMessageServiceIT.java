package de.caritas.cob.messageservice.api.service;

import static com.anarsoft.vmlens.concurrent.junit.TestUtil.runMultithreaded;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.caritas.cob.messageservice.MessageServiceApplication;
import de.caritas.cob.messageservice.api.exception.CustomCryptoException;
import de.caritas.cob.messageservice.api.helper.AuthenticatedUser;
import de.caritas.cob.messageservice.api.model.draftmessage.entity.DraftMessage;
import de.caritas.cob.messageservice.api.repository.DraftMessageRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MessageServiceApplication.class)
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(properties = "spring.profiles.active=testing")
class DraftMessageServiceIT {

  @SpyBean
  private DraftMessageRepository repo;

  @Autowired
  private DraftMessageService draftMessageService;

  @MockBean
  private AuthenticatedUser authenticatedUser;

  @MockBean
  private EncryptionService encryptionService;

  @BeforeEach
  void setup() throws CustomCryptoException {
    doAnswer(encryptArgs -> encryptArgs.getArguments()[0]).when(encryptionService)
        .encrypt(anyString(), anyString());
    doAnswer(decryptArgs -> String.valueOf(decryptArgs.getArguments()[0])).when(encryptionService)
        .decrypt(anyString(), anyString());
    when(this.authenticatedUser.getUserId()).thenReturn("userId");
  }

  @Test
  void saveAndDeleteDraftMessage_Should_produceNoError_When_executionIsInParallel()
      throws InterruptedException {
    AtomicInteger errorCount = new AtomicInteger(0);
    int threadCount = 10;
    String rcGroupId = "rcGroupId";

    runMultithreaded(() -> {
      try {
        draftMessageService.saveDraftMessage("message", rcGroupId, "e2e");
        draftMessageService.deleteDraftMessageIfExist(rcGroupId);
      } catch (Exception e) {
        errorCount.incrementAndGet();
      }
    }, threadCount);

    assertThat(errorCount.get(), is(0));
  }

  @Test
  void should_store_and_load_draft_messages() {
    var rcGroupId = "gvkUGHASLÖD";

    draftMessageService.saveDraftMessage("message", rcGroupId, "e2e");
    var loadedDraftMessage = draftMessageService.findAndDecryptDraftMessage(rcGroupId);

    assertThat(loadedDraftMessage.isPresent(), is(true));
    assertThat(loadedDraftMessage.get().getMessage(), is(("message")));
    assertThat(loadedDraftMessage.get().getT(), is(("e2e")));
  }

  @Test
  void saveAndDeleteDraftMessage_Should_lock_When_executionIsInParallel_With_same_userId_and_rcGroupId()
      throws InterruptedException, ExecutionException, TimeoutException {

    CountDownLatch releaseLatch = new CountDownLatch(1);
    CountDownLatch thread1SaveCalledLatch = new CountDownLatch(1);
    CountDownLatch thread2SaveCalledLatch = new CountDownLatch(1);
    CountDownLatch firstThreadStartedLatch = new CountDownLatch(1);
    final CountDownLatch secondThreadStartedLatch = new CountDownLatch(1);
    String rcGroupId1 = "rcGroupId1";

    // stub out repo.save(...) to fire the latch INSIDE the sync
    doAnswer(invocation -> {
      DraftMessage draftMessage = invocation.getArgument(0);
      if ("m2".equals(draftMessage.getMessage())) {
        thread2SaveCalledLatch.countDown();
      }
      if ("m1".equals(draftMessage.getMessage())) {
        thread1SaveCalledLatch.countDown();
      }
      releaseLatch.await();
      return draftMessage;
    }).when(repo).save(any(DraftMessage.class));

    ExecutorService ex = Executors.newFixedThreadPool(2);

    // 1st thread
    final Future<?> f1 = ex.submit(() -> {
      firstThreadStartedLatch.countDown();
      draftMessageService.saveDraftMessage("m1", rcGroupId1, "t");
    });
    assertTrue(firstThreadStartedLatch.await(1, SECONDS), "1st thread started");
    assertTrue(thread1SaveCalledLatch.await(1, SECONDS), "Thread 1 should have called save");

    // 2nd thread
    final Future<?> f2 = ex.submit(() -> {
      secondThreadStartedLatch.countDown();
      draftMessageService.saveDraftMessage("m2", rcGroupId1, "t");
    });
    assertTrue(secondThreadStartedLatch.await(1, SECONDS), "2nd thread started");
    assertFalse(thread2SaveCalledLatch.await(1, SECONDS), "Thread 2 should not have called save yet (blocked on lock)");

    // unblock thread 1
    releaseLatch.countDown();
    f1.get(5, SECONDS);
    assertTrue(f1.isDone(), "Second thread should be unblocked after the first thread finishes");

    // thread 2 must now complete
    f2.get(5, SECONDS);
    assertTrue(thread2SaveCalledLatch.await(1, SECONDS), "Thread 2 should have called save not blocked anymore");

    ex.shutdown();
  }

  @Test
  void saveAndDeleteDraftMessage_Should_not_lock_When_executionIsInParallel_With_same_rcGroupId_But_Different_userId()
      throws InterruptedException, ExecutionException, TimeoutException {

    CountDownLatch releaseLatch1 = new CountDownLatch(1);
    CountDownLatch releaseLatch2 = new CountDownLatch(1);
    CountDownLatch thread1SaveCalledLatch = new CountDownLatch(1);
    CountDownLatch thread2SaveCalledLatch = new CountDownLatch(1);
    CountDownLatch firstThreadStartedLatch = new CountDownLatch(1);
    final CountDownLatch secondThreadStartedLatch = new CountDownLatch(1);
    String rcGroupId1 = "rcGroupId1";
    final String rcGroupId2 = "rcGroupId2";
    ThreadLocal<String> threadUserId = new ThreadLocal<>();

    when(this.authenticatedUser.getUserId()).thenAnswer(invocation -> threadUserId.get());

    // stub out repo.save(...) to fire the latch INSIDE the sync
    doAnswer(invocation -> {
      DraftMessage draftMessage = invocation.getArgument(0);
      if ("m1".equals(draftMessage.getMessage())) {
        thread1SaveCalledLatch.countDown();
        releaseLatch1.await();
      }
      if ("m2".equals(draftMessage.getMessage())) {
        thread2SaveCalledLatch.countDown();
        releaseLatch2.await();
      }
      return draftMessage;
    }).when(repo).save(any(DraftMessage.class));

    ExecutorService ex = Executors.newFixedThreadPool(2);

    // 1st thread
    final Future<?> f1 = ex.submit(() -> {
      threadUserId.set("userId1");
      firstThreadStartedLatch.countDown();
      draftMessageService.saveDraftMessage("m1", rcGroupId1, "t");
    });
    assertTrue(firstThreadStartedLatch.await(1, SECONDS), "1st thread started");
    assertTrue(thread1SaveCalledLatch.await(1, SECONDS), "Thread 1 should have called save");

    // 2nd thread
    final Future<?> f2 = ex.submit(() -> {
      threadUserId.set("userId2");
      secondThreadStartedLatch.countDown();
      draftMessageService.saveDraftMessage("m2", rcGroupId2, "t");
    });
    assertTrue(secondThreadStartedLatch.await(1, SECONDS), "2nd thread started");
    assertTrue(thread2SaveCalledLatch.await(1, SECONDS), "Thread 2 should not have called save yet (different userId)");

    // unblock thread 1
    releaseLatch1.countDown();
    f1.get(5, SECONDS);
    assertTrue(f1.isDone(), "1st thread done");

    releaseLatch2.countDown();
    f2.get(5, SECONDS);
    assertTrue(f2.isDone(), "2nd thread done");

    ex.shutdown();
  }

  @Test
  void saveAndDeleteDraftMessage_Should_not_lock_When_executionIsInParallel_With_same_userId_But_Different_rcGroupId()
      throws InterruptedException, ExecutionException, TimeoutException {

    CountDownLatch releaseLatch1 = new CountDownLatch(1);
    CountDownLatch releaseLatch2 = new CountDownLatch(1);
    CountDownLatch thread1SaveCalledLatch = new CountDownLatch(1);
    CountDownLatch thread2SaveCalledLatch = new CountDownLatch(1);
    CountDownLatch firstThreadStartedLatch = new CountDownLatch(1);
    final CountDownLatch secondThreadStartedLatch = new CountDownLatch(1);
    String rcGroupId1 = "rcGroupId1";
    String rcGroupId2 = "rcGroupId2";

    // stub out repo.save(...) to fire the latch INSIDE the sync
    doAnswer(invocation -> {
      DraftMessage msg = invocation.getArgument(0);
      if (msg.getRcGroupId().equals(rcGroupId1)) {
        thread1SaveCalledLatch.countDown();
        releaseLatch1.await();
      }
      if (msg.getRcGroupId().equals(rcGroupId2)) {
        thread2SaveCalledLatch.countDown();
        releaseLatch2.await();
      }
      return msg;
    }).when(repo).save(any(DraftMessage.class));

    ExecutorService ex = Executors.newFixedThreadPool(3);

    // 1st thread
    final Future<?> f1 = ex.submit(() -> {
      firstThreadStartedLatch.countDown();
      draftMessageService.saveDraftMessage("m1", rcGroupId1, "t");
    });
    assertTrue(firstThreadStartedLatch.await(1, SECONDS), "1st thread started");
    assertTrue(thread1SaveCalledLatch.await(1, SECONDS), "Thread 1 should have called save");

    // 2nd thread
    final Future<?> f2 = ex.submit(() -> {
      secondThreadStartedLatch.countDown();
      draftMessageService.saveDraftMessage("m2", rcGroupId2, "t");
    });
    assertTrue(secondThreadStartedLatch.await(1, SECONDS), "2nd thread started");
    assertTrue(thread2SaveCalledLatch.await(1, SECONDS), "Thread 2 should have called save (different rcGroupId)");

    releaseLatch1.countDown();
    f1.get(5, SECONDS);
    assertTrue(f1.isDone(), "1st thread done");

    releaseLatch2.countDown();
    f2.get(5, SECONDS);
    assertTrue(f2.isDone(), "2nd thread done");

    ex.shutdown();
  }
}
