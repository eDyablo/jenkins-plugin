package com.earnin.jenkins.plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;

public final class ShellStep extends Step {
  public List<String> cmds = Collections.emptyList();
  public Map<String, String> envs = Collections.emptyMap();
  public boolean quiet = true;
  public boolean echo = false;

  @DataBoundConstructor public ShellStep(final List<String> cmds, final Map<String, String> envs) {
    this.cmds = cmds;
    this.envs = envs;
  }

  public ShellStep(final List<String> cmds) {
    this(cmds, Collections.emptyMap());
  }

  public ShellStep() {
    this(Collections.emptyList(), Collections.emptyMap());
  }

  public void setCmds(final List<String> value) {
    cmds = value;
  }

  @DataBoundSetter
  public void setEnvs(final Map<String, String> value) {
    envs = value;
  }

  @DataBoundSetter
  public void setQuiet(final boolean value) {
    quiet = value;
  }

  @DataBoundSetter
  public void setEcho(final boolean value) {
    echo = value;
  }

  @Override
  public StepExecution start(final StepContext context) throws Exception {
    return new ShellStepExecution(this, context);
  }

  @Extension public static final class DescriptorImpl extends StepDescriptor {
    @Override public String getFunctionName() {
      return "earninShell";
    }

    @Override
    public String getDisplayName() {
      return "Executes arbitrary shell commands";
    }

    @Override
    public Set<? extends Class<?>> getRequiredContext() {
		  return ImmutableSet.of(Launcher.class, TaskListener.class, Run.class);
	  }
  }
}
