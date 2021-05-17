package org.hl7.davinci.refimpl.patientui.services.progress;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the progress of the Payers operations (such as import, refresh, clear data) in an internal map and provides
 * capabilities to manage the cycle of the progress.
 *
 * @author Taras Vuyiv
 */
@Component
public class ProgressManager {

  /**
   * The progress ID associated with operations on data for all payers.
   */
  public static final long ALL_PAYERS_PROGRESS_ID = -1L;

  private final Map<Long, Progress> progressMap = new ConcurrentHashMap<>();

  /**
   * Initializes a new {@link Progress} with the status {@link ProgressStatus#STARTED} for the given Payer.
   *
   * @param progressId the progress ID, typically an ID of the Payer
   * @throws IllegalStateException in case there is already the ongoing operation progress
   */
  public synchronized void init(Long progressId, ProgressType progressType) {
    assertProgressNotActive(get(ALL_PAYERS_PROGRESS_ID));
    assertProgressNotActive(get(progressId));
    getProgressMap().put(progressId, new Progress(progressId, progressType));
  }

  /**
   * Updates the max steps value of the given Payer operation progress.
   *
   * @param progressId the progress ID, typically an ID of the Payer
   * @param maxSteps   the new max steps value
   */
  public void start(Long progressId, int maxSteps) {
    Progress progress = accessProgress(progressId);
    // First we should set the maxSteps here to make sure the 'max' will always be there if anyone is accessing a
    // progress with the STARTED status:
    progress.setMax(maxSteps);
    progress.setStatus(ProgressStatus.STARTED);
  }

  /**
   * Increments the current steps value of the given Payer operation progress.
   *
   * @param progressId the progress ID, typically an ID of the Payer
   */
  public void update(Long progressId) {
    accessProgress(progressId).updateCurrent();
  }

  /**
   * Sets {@link ProgressStatus#COMPLETED} status for the operation progress of the given Payer.
   *
   * @param progressId the progress ID, typically an ID of the Payer
   */
  public void complete(Long progressId) {
    Progress progress = accessProgress(progressId);
    if (!isActive(progress)) {
      throw new IllegalStateException("Only an active progress can be completed.");
    }
    progress.setStatus(ProgressStatus.COMPLETED);
  }

  /**
   * Sets {@link ProgressStatus#FAILED} status for the operation progress of the given Payer.
   *
   * @param progressId   the progress ID, typically an ID of the Payer
   * @param errorMessage the error explanation
   */
  public void fail(Long progressId, String errorMessage) {
    Progress progress = accessProgress(progressId);
    // First we should set the errorMessage here to make sure it will always be there if anyone is accessing a
    // progress with the FAILED status:
    progress.setErrorMessage(errorMessage);
    progress.setStatus(ProgressStatus.FAILED);
  }

  /**
   * @param progressId the progress ID
   * @return the last {@link Progress} of the given Payer or null if does not exist
   */
  public Progress get(Long progressId) {
    return getProgressMap().get(progressId);
  }

  /**
   * Returns all the available progress records.
   *
   * @return a collection of {@link Progress}es
   */
  public Collection<Progress> getAll() {
    return getProgressMap().values();
  }

  /**
   * Removes the progress by the given ID.
   *
   * @param progressId the progress to remove
   */
  public void delete(Long progressId) {
    getProgressMap().remove(progressId);
  }

  protected Progress accessProgress(Long progressId) {
    Progress progress = get(progressId);
    if (progress == null) {
      throw new IllegalStateException("There is no progress with ID '" + progressId + "'.");
    }
    return progress;
  }

  protected Map<Long, Progress> getProgressMap() {
    return progressMap;
  }

  private static void assertProgressNotActive(Progress progress) {
    if (progress != null && isActive(progress)) {
      throw new IllegalStateException("There could be only one operation in progress at the same time.");
    }
  }

  private static boolean isActive(Progress progress) {
    ProgressStatus status = progress.getStatus();
    return status == ProgressStatus.NEW || status == ProgressStatus.STARTED;
  }
}
