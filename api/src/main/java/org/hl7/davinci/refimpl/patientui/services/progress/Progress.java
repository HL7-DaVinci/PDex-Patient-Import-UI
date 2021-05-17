package org.hl7.davinci.refimpl.patientui.services.progress;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The model of the Payer operations progress.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
public class Progress {

  private final long id;
  private final ProgressType type;

  private final AtomicInteger current;
  private int max;

  private ProgressStatus status;
  private String errorMessage;

  public Progress(Long id, ProgressType type) {
    if (id == null) {
      throw new IllegalArgumentException("The id cannot be null.");
    }
    this.id = id;
    this.type = type;
    this.current = new AtomicInteger();
    this.status = ProgressStatus.NEW;
  }

  public int getCurrent() {
    return current.get();
  }

  /**
   * Increments the value of <code>current<code/>.
   */
  public void updateCurrent() {
    current.incrementAndGet();
  }
}
