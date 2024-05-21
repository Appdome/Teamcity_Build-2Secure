package jetbrains.teamcity;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jetbrains.teamcity.EchoRunnerConstants.*;

public class EchoRunner extends RunType {

  private final PluginDescriptor descriptor;

  public EchoRunner(RunTypeRegistry registry, PluginDescriptor descriptor) {
    this.descriptor = descriptor;
    registry.registerRunType(this);
  }

  @NotNull
  @Override
  public String getType() {
    return EchoRunnerConstants.RUNNER_TYPE;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Appdome Build-2Secure";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Build protected mobile apps using Appdome";
  }

  @Nullable
  @Override
  public PropertiesProcessor getRunnerPropertiesProcessor() {

    return properties -> {
      final List<InvalidProperty> invalidProperties = new ArrayList<>();

      final String app_location = properties.get(APP_LOCATION);
      if (app_location == null) {
        invalidProperties.add(new InvalidProperty(APP_LOCATION, "Should not be null"));
      }

      final String fusionSet = properties.get(FUSION_SET);
      if (fusionSet == null) {
        invalidProperties.add(new InvalidProperty(FUSION_SET, "Should not be null"));
      }

      final String googleFP = properties.get(GOOGLE_PLAY_FINGERPRINT);
      final String googleSign = properties.get(GOOGLE_SIGN);
      if ((googleSign != null) && (googleFP == null)) {
          invalidProperties.add(new InvalidProperty(GOOGLE_PLAY_FINGERPRINT, "Should not be null if Google Sign is selected."));
      }
      return invalidProperties;
    };
  }

  @Nullable
  @Override
  public String getEditRunnerParamsJspFilePath() {
      return descriptor.getPluginResourcesPath("editEchoRunnerParameters.jsp");
  }

  @Nullable
  @Override
  public String getViewRunnerParamsJspFilePath() {
    return descriptor.getPluginResourcesPath("viewEchoRunnerParameters.jsp");
  }
  @Nullable
  @Override
  public Map<String, String> getDefaultRunnerProperties() {
    return new HashMap<>();
  }

  @NotNull
  @Override

  public String describeParameters(@NotNull Map<String, String> parameters) {
    String Platform = parameters.get(PLATFORM);
    String AppLocation = parameters.get(APP_LOCATION);
    AppLocation = (AppLocation == null) ? "Not provided": "Provided";
    String OutputFileName = parameters.get(OUTPUT_FILE_NAME);
    OutputFileName = (OutputFileName == null) ? "Use default name": OutputFileName;
    String FP;
    String TeamId = parameters.get(TEAM_ID);
    TeamId = (TeamId == null) ? "Not provided": TeamId;
    String AppId = parameters.get(APP_ID);
    AppId = (AppId == null) ? "Not provided": AppId;
    String GoogleSign = parameters.get(GOOGLE_SIGN);
    if (GoogleSign == null) {
      GoogleSign = "false";
      FP = parameters.get(FINGERPRINT);
    } else {
      GoogleSign = "true";
      FP = parameters.get(GOOGLE_PLAY_FINGERPRINT);
    }
    FP = (FP == null) ? "Not provided": "Provided";
    String FS = parameters.get(FUSION_SET);
    String KSF = parameters.get(KEYSTORE_FILE_LOCATION);
    KSF = (KSF == null) ? "Not provided": "Provided";
    String KSA = parameters.get(KEYSTORE_ALIAS);
    KSA = (KSA == null) ? "Not provided": "*****";
    String KP = parameters.get(KEY_PASS);
    KP = (KP == null) ? "Not provided": "*****";
    String KSP = parameters.get(KEYSTORE_PASS);
    KSP = (KSP == null) ? "Not provided": "*****";
    String BuildLogs = parameters.get(BUILD_LOGS);
    BuildLogs = (BuildLogs == null) ? "false":"true";
    String MP = parameters.get(PROVISIONING_FILE_LOCATION);
    MP = (MP == null) ? "Not provided": "Provided";
    String CERT = parameters.get(CERT_FILE_LOCATION);
    CERT = (CERT == null) ? "Not provided": "Provided";
    String CERTPASS = parameters.get(CERT_PASS);
    CERTPASS = (CERTPASS == null) ? "Not provided": "*****";
    String ENT = parameters.get(ENT_FILE_LOCATION);
    ENT = (ENT == null) ? "Not provided": "Provided";
    String SignType = parameters.get(SIGN_TYPE);
    String Build2Test = parameters.get(BUILD_TO_TEST);
    String SO = parameters.get(SECONDARY_OUTPUT);
    if (SO == null) {
      SO = "false";
    } else {
      if (OutputFileName == "Use default name") {
        SO = "Appdome_Universal.apk";
      } else {
        SO = OutputFileName + "_Universal.apk";
      }
    }

    if (SignType.equals("Auto-Dev-Sign")) {
      SO = "false";
    }

    String description = "App Location: " + AppLocation
              + "\nPlatform: " + Platform
              + "\nOutput File Name: " + OutputFileName
              + "\nFusion Set: " + FS
              + "\nTeam ID: " + TeamId
              + "\nSign Type: " + SignType;

    if (Platform.equals("Android")) {
      description += "\nFirebase App ID: " + AppId
              + "\nGoogle Sign: " + GoogleSign
              + "\nFingerprint: " + FP
              + "\nSecondary Output: " + SO
              + "\nKeystore File: " + KSF
              + "\nKey Store Password: " + KSP
              + "\nKey Store Alias: " + KSA
              + "\nKey Password: " + KP;
    } else {
      description += "\nMobile Provisioning File: " + MP
              + "\nSinging Certificate File: " + CERT
              + "\nKeystore Password: " + CERTPASS
              + "\nEntitlement File: " + ENT;
    }

    description += "\nBuild with logs: " + BuildLogs
              + "\nBuild to Test: " + Build2Test;

    return description;
  }
}
