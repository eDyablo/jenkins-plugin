package com.earnin.jenkins.plugin;

import java.io.ByteArrayOutputStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import hudson.Launcher;
import hudson.Proc;
import hudson.model.Run;
import hudson.model.TaskListener;

public final class ShellStepExecution extends StepExecution {
  private static final long serialVersionUID = 1L;

  private transient ShellStep step;

  public ShellStepExecution(final ShellStep step, final StepContext context) {
    super(context);
    this.step = step;
  }

  @Override
  public boolean start() throws Exception {
    final StepContext context = getContext();
    try {
      context.onSuccess(launch());
    } catch (final Throwable cause) {
      context.onFailure(cause);
    }
    return true;
  }

  @Override
  public void stop(final Throwable cause) throws Exception {
    getContext().onFailure(cause);
  }

  private String launch() throws Exception {
    final StepContext context = getContext();
    final TaskListener listener = context.get(TaskListener.class);
    final Launcher launcher = context.get(Launcher.class);
    final Run<?,?> run = context.get(Run.class);
    if (!launcher.isUnix()) {
      throw new ShellStepException("Non-linux environment");
    }
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final ByteArrayOutputStream error = new ByteArrayOutputStream();
    final Proc process = launcher.launch()
      .cmds(step.cmds)
      .envs(run.getEnvironment(listener))
      .envs(step.envs)
      .stdout(step.echo ?
        new TeeOutputStream(listener.getLogger(), output) : output)
      .stderr(error)
      .quiet(step.quiet)
      .start();
    final int exitCode = process.join();
    if (exitCode != 0) {
      throw new ShellStepException(error.toString("UTF-8"));
    }
    return output.toString("UTF-8").trim();
  }
}