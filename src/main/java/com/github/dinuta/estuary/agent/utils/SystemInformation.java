package com.github.dinuta.estuary.agent.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SystemInformation {
    private static final Logger log = LoggerFactory.getLogger(SystemInformation.class);

    public static final Map<String, String> getSystemInfo() {

        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.info("Could not detect hostname: " + ExceptionUtils.getStackTrace(e));
        }
        String layer = new File("/.dockerenv").exists() ? "Docker" : "Virtual Machine";
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024 * 1024);
        maxMemory = maxMemory == Long.MAX_VALUE ? Long.MAX_VALUE : maxMemory;

        Map<String, String> info = new LinkedHashMap<>();
        info.put("system", getSystem());
        info.put("platform", "NA");
        info.put("release", "NA");
        info.put("version", System.getProperty("os.version"));
        info.put("architecture", System.getProperty("os.arch"));
        info.put("machine", "NA");
        info.put("layer", layer);
        info.put("hostname", hostname);
        info.put("cpu", System.getenv("PROCESSOR_IDENTIFIER") != null ? System.getenv("PROCESSOR_IDENTIFIER") : "NA");
        info.put("ram", maxMemory + " GB");
        info.put("java", System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version"));

        return info;
    }

    private static String getSystem() {
        String PLATFORM_NAME = System.getProperty("os.name").toLowerCase();
        String detectedOs;
        if (PLATFORM_NAME.contains("win")) {
            detectedOs = "Windows";
        } else if (PLATFORM_NAME.contains("lin")) {
            detectedOs = "Linux";
        } else {
            detectedOs = "Other";
        }

        return detectedOs;
    }
}