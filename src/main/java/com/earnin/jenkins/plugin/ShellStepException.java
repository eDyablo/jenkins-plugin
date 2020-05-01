package com.earnin.jenkins.plugin;

public final class ShellStepException extends Exception {
  private static final long serialVersionUID = 1L;

  public ShellStepException(final String message) {
    super(message);
  }
}
