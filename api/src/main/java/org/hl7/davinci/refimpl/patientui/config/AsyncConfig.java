package org.hl7.davinci.refimpl.patientui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The configuration of the asynchronous service methods behavior.
 *
 * @author Taras Vuyiv
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {}
