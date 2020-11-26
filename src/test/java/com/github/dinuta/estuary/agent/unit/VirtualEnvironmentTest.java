package com.github.dinuta.estuary.agent.unit;

import com.github.dinuta.estuary.agent.component.VirtualEnvironment;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class VirtualEnvironmentTest {

    @Test
    public void whenSettingEnvVarsAndTheEnvIsCleanThenAllEnvVarsAreSet() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();
        envVarsToBeSet.put("FOO1", "BAR1");
        envVarsToBeSet.put("FOO2", "BAR2");

        Map<String, String> environment = new VirtualEnvironment().setExternalEnvVars(envVarsToBeSet);

        assertThat(environment).isEqualTo(envVarsToBeSet);
    }

    @Test
    public void whenSettingAlreadyExistingSystemEnvVarThenIsNotSet() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();
        envVarsToBeSet.put("FOO1", "BAR1");
        envVarsToBeSet.put("JAVA_HOME", "BAR2"); // <- system one

        Map<String, String> environment = new VirtualEnvironment().setExternalEnvVars(envVarsToBeSet);

        assertThat(environment).isNotEqualTo(envVarsToBeSet);
    }

    @Test
    public void whenSettingTwiceSameVirtualEnvVarThenIsSetEveryTime() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();
        envVarsToBeSet.put("FOO1", "BAR1");

        VirtualEnvironment environment = new VirtualEnvironment();
        Map<String, String> take1 = environment.setExternalEnvVars(envVarsToBeSet);
        Map<String, String> take2 = environment.setExternalEnvVars(envVarsToBeSet);

        assertThat(take1).isEqualTo(envVarsToBeSet);
        assertThat(take2).isEqualTo(envVarsToBeSet);
    }


    @Test
    public void whenSettingEmptyMapVirtualEnvVarThenResponseIsEmpty() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();

        VirtualEnvironment environment = new VirtualEnvironment();
        Map<String, String> envVarsAdded = environment.setExternalEnvVars(envVarsToBeSet);

        assertThat(envVarsAdded).isEqualTo(envVarsToBeSet);
    }

    @Test
    public void whenSettingVirtualEnvVarsThenAHardLimitIsReached() {
        final int VIRTUAL_ENV_VARS_LIMIT_SIZE = VirtualEnvironment.VIRTUAL_ENVIRONMENT_MAX_SIZE;
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();

        for (int i = 0; i < 2 * VIRTUAL_ENV_VARS_LIMIT_SIZE; i++) {
            envVarsToBeSet.put(String.valueOf(i), String.valueOf(i));
        }

        VirtualEnvironment environment = new VirtualEnvironment();
        Map<String, String> envVarsAdded = environment.setExternalEnvVars(envVarsToBeSet);

        assertThat(envVarsAdded).isNotEqualTo(envVarsToBeSet);
        assertThat(envVarsAdded.get(String.valueOf(VIRTUAL_ENV_VARS_LIMIT_SIZE))).isEqualTo(null);
    }

}
