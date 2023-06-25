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

  @NotNull
  @Override
//  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
//
//    final String message = getRunnerParameters().get(EchoRunnerConstants.MESSAGE_KEY);
//
//    final String scriptContent = "/bin/echo " + message + " | tee echo.txt";
//
//    final String script = getCustomScript(scriptContent);
//
//    setExecutableAttribute(script);
//
//    return new SimpleProgramCommandLine(getRunnerContext(), script, Collections.<String>emptyList());
//  }

  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

    String localDir = getWorkingDirectory().getAbsolutePath() + "/appdome-api-bash";
    try {
      FileUtils.deleteDirectory(new File(localDir));
    } catch (IOException e) {}

    // clone to Appdome Git repository
    try {
      GitCloneAppdomeAPI(localDir);
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
    if (Build2Test == null) {
      Build2Test = "None";
    }
    if (Build2Test.equals("None")) {
      Build2Test = "";
    } else {
      Build2Test = " --build_to_test " + Build2Test.toLowerCase();
    }

    final String Platform = getRunnerParameters().get(EchoRunnerConstants.PLATFORM);

    // android related params

    final String Fingerprint = getRunnerParameters().get(EchoRunnerConstants.FINGERPRINT);
    String SigningFingerprint = (Fingerprint == null) ? "": " --signing_fingerprint " + Fingerprint;

    String GoogleSign = getRunnerParameters().get(EchoRunnerConstants.GOOGLE_SIGN);
    if (GoogleSign == null) {
      GoogleSign = "";
    } else {
      GoogleSign = " --google_play_signing ";
      String GooglePlayFingerprint = getRunnerParameters().get(EchoRunnerConstants.GOOGLE_PLAY_FINGERPRINT);
      SigningFingerprint = " --signing_fingerprint " + GooglePlayFingerprint;
    }

    String KeystoreLocation;
    String Keystore = "";
    String KeystorePass = "";
    String KeystoreAlias = "";
    String KeyPass = "";

    // ios related params

    String ProvisioningProfile;
    String Entitelements;

    // signing type
    String SingDetails;
    String SignType = getRunnerParameters().get(EchoRunnerConstants.SIGN_TYPE);
    switch (SignType) {
      case "Private-Sign":
        SignType = " --private_signing";
        if (Platform.equals("Android")) {
          SingDetails = SignType
                      + GoogleSign
                      + SigningFingerprint;
        } else {
          try {
            String ProvisioningFilesLocation = getRunnerParameters().get(EchoRunnerConstants.PROVISIONING_FILE_LOCATION);
            ProvisioningProfile = VarsToFiles(ProvisioningFilesLocation, "--provisioning_profiles", localDir);
            String EntFileLocation = getRunnerParameters().get(EchoRunnerConstants.ENT_FILE_LOCATION);
            Entitelements =  VarsToFiles(EntFileLocation, "--entitlements", localDir);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          SingDetails = SignType
                      + ProvisioningProfile
                      + Entitelements;
        }
        break;
      case "Auto-Dev-Sign":
        SignType = " --auto_dev_private_signing";
        if (Platform.equals("Android")) {
          SingDetails = SignType
                      + GoogleSign
                      + SigningFingerprint;
        } else {
          try {
            String ProvisioningFilesLocation = getRunnerParameters().get(EchoRunnerConstants.PROVISIONING_FILE_LOCATION);
            ProvisioningProfile = VarsToFiles(ProvisioningFilesLocation, "--provisioning_profiles", localDir);
            String EntFileLocation = getRunnerParameters().get(EchoRunnerConstants.ENT_FILE_LOCATION);
            Entitelements =  VarsToFiles(EntFileLocation, "--entitlements", localDir);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          SingDetails = SignType
                      + ProvisioningProfile
                      + Entitelements;
        }
        break;
      default:
        SignType = " --sign_on_appdome";
        if (Platform.equals("Android")) {
          KeystoreLocation = getRunnerParameters().get(EchoRunnerConstants.KEYSTORE_FILE_LOCATION);
          try {
            Keystore = VarsToFiles(KeystoreLocation, "--keystore", localDir);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          KeystorePass = " --keystore_pass " + EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.KEYSTORE_PASS));


          KeystoreAlias = " --keystore_alias " + EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.KEYSTORE_ALIAS));
          KeyPass = " --key_pass " + EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.KEY_PASS));
          SingDetails = SignType
                      + Keystore
                      + KeystorePass
                      + KeystoreAlias
                      + KeyPass
                      + GoogleSign
                      + SigningFingerprint;
        } else {
          KeystoreLocation = getRunnerParameters().get(EchoRunnerConstants.CERT_FILE_LOCATION);
          try {
            Keystore = VarsToFiles(KeystoreLocation, "--keystore", localDir);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          KeystorePass = " --keystore_pass " + EnvVarToParam(getRunnerParameters().get(EchoRunnerConstants.CERT_PASS));
          try {
            String ProvisioningFilesLocation = getRunnerParameters().get(EchoRunnerConstants.PROVISIONING_FILE_LOCATION);
            ProvisioningProfile = VarsToFiles(ProvisioningFilesLocation, "--provisioning_profiles", localDir);
            String EntFileLocation = getRunnerParameters().get(EchoRunnerConstants.ENT_FILE_LOCATION);
            Entitelements =  VarsToFiles(EntFileLocation, "--entitlements", localDir);
          } catch (IOException e) {
          throw new RuntimeException(e);
          }
          SingDetails = SignType
                      + Keystore
                      + KeystorePass
                      + ProvisioningProfile
                      + Entitelements;
        }
        break;
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
    String FusedAppFile = localDir + "/Appdome_" + VanillaFileName;
    setOutputEnv("APPDOME_BUILD", FusedAppFile);
    String CertSecureFile = localDir + "/certificate.pdf";
    String App = "--app " + AppFileLocal;
    String OutputFile = " --output " + FusedAppFile;
    String CertOutput = " --certificate_output " + CertSecureFile;
    String scriptContent = "cd " + localDir + "; ./appdome_api.sh ";

    scriptContent += App
                  + ApiKey
                  + FusionSetId
                  + TeamId
                  + BuildLogs
                  + SingDetails;

    scriptContent += OutputFile + CertOutput + " | tee appdome.log";

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

  private String VarsToFiles(String EnvVars, String param, String directory) throws IOException {
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
