package jetbrains.teamcity;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agentServer.AgentBuild;
import jetbrains.buildServer.artifacts.ArtifactDependencyInfo;
import jetbrains.buildServer.parameters.ValueResolver;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.Option;
import jetbrains.buildServer.util.PasswordReplacer;
import jetbrains.buildServer.util.TCStreamUtil;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.vcs.VcsRootEntry;
import jetbrains.buildServer.xmlrpc.NodeIdHolder;
import org.apache.xpath.operations.Bool;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.net.URL;


public class EchoService extends BuildServiceAdapter {

  private final Set<File> myFilesToDelete = new HashSet<File>();

  private String CollectSigningDetailsAndroid(String localDir, Boolean isAAB) {
    String Fingerprint = getRunnerParameters().get(EchoRunnerConstants.FINGERPRINT);
    String SigningFingerprint = (Fingerprint == null) ? "": " --signing_fingerprint " + Fingerprint;
    String GoogleSign = getRunnerParameters().get(EchoRunnerConstants.GOOGLE_SIGN);
    if (GoogleSign == null) {
      GoogleSign = "";
    } else {
      String GooglePlayFingerprint = getRunnerParameters().get(EchoRunnerConstants.GOOGLE_PLAY_FINGERPRINT);
      GoogleSign = " --google_play_signing --signing_fingerprint " + GooglePlayFingerprint;
      SigningFingerprint = "";
    }

    String SignType = getRunnerParameters().get(EchoRunnerConstants.SIGN_TYPE);
    String SingDetails;
    switch (SignType) {
      case "Private-Sign":
        SingDetails = " --private_signing";
        break;
      case "Auto-Dev-Sign":
        SingDetails = " --auto_dev_private_signing";
        break;
      default:
        String KeystoreLocation = getRunnerParameters().get(EchoRunnerConstants.KEYSTORE_FILE_LOCATION);
        String Keystore;
        try {
          Keystore = VarsToFiles(KeystoreLocation, "--keystore", localDir);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        String KeystorePass = " --keystore_pass " + EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.KEYSTORE_PASS));
        String KeystoreAlias = " --keystore_alias " + EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.KEYSTORE_ALIAS));
        String KeyPass = " --key_pass " + EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.KEY_PASS));
        SingDetails = " --sign_on_appdome"
                + Keystore
                + KeystorePass
                + KeystoreAlias
                + KeyPass;
        break;
    }

    String SecondaryOutput = getRunnerParameters().get(EchoRunnerConstants.SECONDARY_OUTPUT);
    if ((SecondaryOutput != null) && isAAB && !SignType.equals("Auto-Dev-Sign")) {
      String OutputFileName = getRunnerParameters().get(EchoRunnerConstants.OUTPUT_FILE_NAME);
      String outputDir = getWorkingDirectory().getAbsolutePath();
      String artifactsDir = outputDir + "/artifacts/";
      String BuildSO;
      if (OutputFileName == null) {
        BuildSO = artifactsDir + "Appdome_Universal.apk";
      } else {
        BuildSO = artifactsDir + OutputFileName + "_Universal.apk";
      }
      SecondaryOutput = " --second_output " + BuildSO;
      setOutputEnv("APPDOME_BUILD_SO", BuildSO);
    } else {
      SecondaryOutput = "";
    }

    SingDetails += GoogleSign
                + SigningFingerprint
                + SecondaryOutput;
    return SingDetails;
  }

  private String CollectSigningDetailsIOS(String localDir) {
    String ProvisioningFilesLocation = getRunnerParameters().get(EchoRunnerConstants.PROVISIONING_FILE_LOCATION);
    String EntFileLocation;
    String ProvisioningProfile;
    String Entitelements;
    String SingDetails;
    try {
      ProvisioningProfile = VarsToFiles(ProvisioningFilesLocation, "--provisioning_profiles", localDir);
      EntFileLocation = getRunnerParameters().get(EchoRunnerConstants.ENT_FILE_LOCATION);
      Entitelements =  VarsToFiles(EntFileLocation, "--entitlements", localDir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String SignType = getRunnerParameters().get(EchoRunnerConstants.SIGN_TYPE);
    switch (SignType) {
      case "Private-Sign":
        SingDetails = " --private_signing";
        break;
      case "Auto-Dev-Sign":
        SingDetails = " --auto_dev_private_signing";
        break;
      default:
        String Keystore;
        String KeystoreLocation = getRunnerParameters().get(EchoRunnerConstants.CERT_FILE_LOCATION);
        try {
          Keystore = VarsToFiles(KeystoreLocation, "--keystore", localDir);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        String KeystorePass = " --keystore_pass " + EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.CERT_PASS));
        SingDetails = " --sign_on_appdome"
                + Keystore
                + KeystorePass;
        break;
    }
    SingDetails += ProvisioningProfile
                + Entitelements;
    return SingDetails;
  }

  public void RemoveOldArtifacts(String Directory) {
    File folder = new File(Directory);
    ArrayList <File> toDelete = new ArrayList<File>();
    File[] files = folder.listFiles();
    for (File file: files) {
      if (file.getName().matches(".*.aab") || file.getName().matches(".*.apk") || file.getName().matches(".*.ipa") || file.getName().matches(".*.pdf")) {
        toDelete.add(file);
      }
    }
    for (File file: toDelete) {
      file.delete();
    }
  }

  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
    setOutputEnv("APPDOME_CLIENT_HEADER", "TeamCity/1.1.2");
    String outputDir = getWorkingDirectory().getAbsolutePath();
    String artifactsDir = outputDir + "/artifacts/";
    String localDir = outputDir + "/appdome-api-bash";
    try {
      FileUtils.deleteDirectory(new File(localDir));
      RemoveOldArtifacts(outputDir);
      FileUtils.deleteDirectory(new File(artifactsDir));
    } catch (IOException e) {}

    // clone to Appdome Git repository
    try {
      GitCloneAppdomeAPI(localDir);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    Boolean debug = false;
    try {
      String debugOnly = getEnvironmentVariables().get("APPDOME_DEBUG");
      if (debugOnly.equals("1")) {
        debug = true;
      }
    } catch (Exception e) {}

    try {
      FileUtils.forceMkdir(new File(artifactsDir));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    String ApiKey = " --api_key " + getEnvironmentVariables().get("api_key");
    String FusionSetId = " --fusion_set_id " + getRunnerParameters().get(EchoRunnerConstants.FUSION_SET);
    String TeamId = getRunnerParameters().get(EchoRunnerConstants.TEAM_ID);
    TeamId = (TeamId == null) ? "": " --team_id " + TeamId;

    String BuildLogs = getRunnerParameters().get(EchoRunnerConstants.BUILD_LOGS);
    BuildLogs = (BuildLogs == null) ? "": " --build_logs ";

    String Build2Test = getRunnerParameters().get(EchoRunnerConstants.BUILD_TO_TEST);
    if (Build2Test.equals("None")) {
      Build2Test = "";
    } else {
      Build2Test = " -btv " + Build2Test.toLowerCase();
    }

    // download app file

    String AppLocation = EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.APP_LOCATION));
    String AppFileLocal;
    try {
      AppFileLocal = downloadFile(AppLocation, localDir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String VanillaFileName = new File(AppFileLocal).getName();
    String AppType = VanillaFileName.substring(VanillaFileName.lastIndexOf(".")+1);
    String AppName = VanillaFileName.substring(0, VanillaFileName.lastIndexOf("."));
    Boolean isAAB = (AppType.equals("aab")) ? true:false;

    final String Platform = getRunnerParameters().get(EchoRunnerConstants.PLATFORM);
    String SignDetails;
    if (Platform.equals("Android")) {
      SignDetails = CollectSigningDetailsAndroid(localDir, isAAB);
    } else {
      SignDetails = CollectSigningDetailsIOS(localDir);
    }

    String SignType = getRunnerParameters().get(EchoRunnerConstants.SIGN_TYPE);
    String extension = AppType;
    if (SignType.equals("Auto-Dev-Sign")) {
      extension = "sh";
    }
    String OutputFileName = getRunnerParameters().get(EchoRunnerConstants.OUTPUT_FILE_NAME);
    String FusedAppFile = (OutputFileName == null) ? artifactsDir + "Appdome_" + AppName : artifactsDir + OutputFileName;
    FusedAppFile = FusedAppFile + "." + extension;
    setOutputEnv("APPDOME_BUILD", FusedAppFile);
    String CertSecureFile = artifactsDir + "certificate.pdf";
    String App = "--app " + AppFileLocal;
    String OutputFile = " --output " + FusedAppFile;
    String CertOutput = " --certificate_output " + CertSecureFile;
    String scriptContent = "cd " + localDir + "; ./appdome_api.sh ";
    if (debug) {
      scriptContent = "echo ";
    }

    scriptContent += App
                  + ApiKey
                  + FusionSetId
                  + TeamId
                  + BuildLogs
                  + SignDetails
                  + Build2Test;

    scriptContent += OutputFile + CertOutput + " | tee ../appdome.log && cd .. && rm -rf appdome-api-bash";

    String script = getCustomScript(scriptContent);
    setExecutableAttribute(script);
    return new SimpleProgramCommandLine(getRunnerContext(), script, Collections.<String>emptyList());
  }

  String getCustomScript(String scriptContent) throws RunBuildException {
    try {
      final File scriptFile = File.createTempFile("custom_script", ".sh", getAgentTempDirectory());
      FileUtil.writeFileAndReportErrors(scriptFile, scriptContent);
      myFilesToDelete.add(scriptFile);
      return scriptFile.getAbsolutePath();
    } catch (IOException e) {
      RunBuildException exception = new RunBuildException("Failed to create temporary custom script file in directory '" + getAgentTempDirectory() + "': " + e
          .toString(), e);
      exception.setLogStacktrace(false);
      throw exception;
    }
  }

  private void setExecutableAttribute(@NotNull final String script) throws RunBuildException {
    try {
      TCStreamUtil.setFileMode(new File(script), "a+x");
    } catch (Throwable t) {
      throw new RunBuildException("Failed to set executable attribute for custom script '" + script + "'", t);
    }
  }

  @Override
  public void afterProcessFinished() throws RunBuildException {
    super.afterProcessFinished();
    for (File file : myFilesToDelete) {
      FileUtil.delete(file);
    }
    myFilesToDelete.clear();
  }

  public void setOutputEnv(String EnvName, String BuildOutput) {
    AgentRunningBuild agentRunningBuild = getRunnerContext().getBuild();
    agentRunningBuild.addSharedEnvironmentVariable(EnvName, BuildOutput);
  }

  public static void GitCloneAppdomeAPI(String destinationPath) throws Exception {
    File file = new File("appdome-api-bash");
    try {
      FileUtils.deleteDirectory(file);
    } catch (Exception e) {}

    final String repositoryUrl = EchoRunnerConstants.AppdomeRepoURL;

    try {
      // Build the git clone command
      String[] gitCloneCommand = { "git", "clone", repositoryUrl, destinationPath };

      // Execute the git clone command
      Process process = Runtime.getRuntime().exec(gitCloneCommand);

      // Get the command output
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      // Wait for the command to finish
      int exitCode = process.waitFor();
      System.out.println("Git clone completed with exit code: " + exitCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String downloadFile(String fileUrl, String saveDirectory) throws IOException {
    if (!fileUrl.startsWith("http") && !fileUrl.startsWith("HTTP")) {
      // assuming the file URL is a local path
      return fileUrl;
    }
    URL url = new URL(fileUrl);
    String fileName = getFileNameFromUrl(fileUrl);
    String savePath = saveDirectory + File.separator + fileName;

    try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
         FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {

      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
        fileOutputStream.write(buffer, 0, bytesRead);
      }
    }
    return savePath;
  }

  private static String getFileNameFromUrl(String fileUrl) {
    String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    fileName = fileName.split("\\?")[0]; // Remove query parameters if any
    return fileName;
  }

  private String EnvVarToParam(String var) {
    String param;
    if (var.startsWith("env.")) {
      int i = var.indexOf('.');
      param = getEnvironmentVariables().get(var.substring(i+1));
    } else {
      param = var;
    }
    return param;
  }

  public String VarsToFiles(String EnvVars, String param, String directory) throws IOException {
    String file;
    String[] Evars = EnvVars.split(",");
    ArrayList<String> files = new ArrayList<String>();
    for (String Evar: Evars) {
      if (Evar.startsWith("env.")) {
        int i = Evar.indexOf('.');
        file = getEnvironmentVariables().get(Evar.substring(i+1));
      } else {
        file = Evar;
      }
      files.add(file);
    }
    return getParamWithFiles(files, param, directory);
  }

  private String getParamWithFiles(ArrayList<String> files, String param, String directory) throws IOException {
    if (files.isEmpty()) {
      return "";
    }
    String FileNames = "";
    String downloadedFile;
    for (String file: files) {
      downloadedFile = downloadFile(file, directory);
      FileNames += downloadedFile + ",";
    }
    FileNames = FileNames.substring(0, FileNames.length()-1);
    return " " + param + " " + FileNames;
  }

}
