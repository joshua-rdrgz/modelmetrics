import * as Comlink from 'comlink';

class StopwatchWorker {
  private startTime: number | null = null;
  private elapsedTime = 0;
  private timerInterval: NodeJS.Timeout | null = null;

  /**
   * Starts or resumes the stopwatch.
   * @param {number} initialElapsedTime - The initial elapsed time in milliseconds.
   */
  start(initialElapsedTime: number = 0) {
    this.elapsedTime = initialElapsedTime;
    this.startTime = Date.now() - this.elapsedTime;
    if (this.timerInterval === null) {
      this.timerInterval = setInterval(() => {
        this.elapsedTime = Date.now() - this.startTime!;
      }, 10);
    }
  }

  /**
   * Stops the stopwatch.
   */
  stop() {
    if (this.timerInterval !== null) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  /**
   * Gets the current elapsed time.
   * @returns {number} The current elapsed time in milliseconds.
   */
  getElapsedTime(): number {
    return this.elapsedTime;
  }
}

Comlink.expose(new StopwatchWorker());
