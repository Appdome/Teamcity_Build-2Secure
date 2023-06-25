package jetbrains.teamcity;

public interface EchoRunnerConstants {

  String RUNNER_TYPE = "EchoRunner";
  String PLATFORM = "platform";
  String MESSAGE_KEY = "echoMessageKey";
  String FINGERPRINT = "fingerprint";
  String GOOGLE_PLAY_FINGERPRINT = "google_play_fingerprint";
  String GOOGLE_SIGN = "google_sign";
  String SIGN_TYPE = "sign_type";
  String BUILD_LOGS = "build_logs";
  String APP_LOCATION = "app_location";
  String KEYSTORE_FILE_LOCATION = "keystore_file_location";
  String KEYSTORE_PASS = "keystore_pass";
  String KEYSTORE_ALIAS = "keystore_alias";
  String KEY_PASS = "key_pass";
  String API_KEY = System.getenv("api_key");
  String FUSION_SET = "fusion_set";
  String TEAM_ID = "team_id";
  String PROVISIONING_FILE_LOCATION = "provisioning_file_location";
  String CERT_FILE_LOCATION = "cert_file_location";
  String CERT_PASS = "cert_pass";
  String ENT_FILE_LOCATION = "ent_file_location";
  String BUILD_TO_TEST = "build2test";
  String APPDOME_BUILD = System.getenv("APPDOME_BUILD");
  String AppdomeRepoURL = "https://github.com/Appdome/appdome-api-bash.git";
}
