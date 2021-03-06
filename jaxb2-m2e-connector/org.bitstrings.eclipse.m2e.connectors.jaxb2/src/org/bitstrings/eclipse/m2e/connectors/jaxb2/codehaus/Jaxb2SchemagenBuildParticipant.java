/*
 *
 *    Copyright (c) 2011 bitstrings.org - Pino Silvaggio
 *
 *    All rights reserved. This program and the accompanying materials
 *    are made available under the terms of the Eclipse Public License v1.0
 *    which accompanies this distribution, and is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.bitstrings.eclipse.m2e.connectors.jaxb2.codehaus;

import java.io.File;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.bitstrings.eclipse.m2e.common.BuildHelper;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class Jaxb2SchemagenBuildParticipant extends MojoExecutionBuildParticipant
{
    public Jaxb2SchemagenBuildParticipant(MojoExecution execution)
    {
        super(execution, true);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception
    {
        final IMaven maven = MavenPlugin.getMaven();
        final BuildContext buildContext = getBuildContext();
        final MavenSession mavenSession = getSession();
        final MojoExecution mojoExecution = getMojoExecution();

        for (final String source : mavenSession.getCurrentProject().getCompileSourceRoots())
        {
            if (!ArrayUtils.isEmpty(
                            BuildHelper.getModifiedFiles(
                                            mavenSession, mojoExecution,
                                            maven, buildContext,
                                            new File(source),
                                            "includes",
                                            "excludes")))
            {
                final Set<IProject> result = super.build(kind, monitor);

                final File generated =
                                maven.getMojoParameterValue(
                                                mavenSession, mojoExecution,
                                                "outputDirectory",
                                                File.class);

                if (generated != null)
                {
                    buildContext.refresh(generated);
                }

                return result;
            }
        }

        return null;
    }
}
