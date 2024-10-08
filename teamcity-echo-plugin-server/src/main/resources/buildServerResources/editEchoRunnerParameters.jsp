<%@ page import="jetbrains.teamcity.EchoRunnerConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<c:set var="fingerprint" value="<%=EchoRunnerConstants.FINGERPRINT%>"/>
<c:set var="google_play_fingerprint" value="<%=EchoRunnerConstants.GOOGLE_PLAY_FINGERPRINT%>"/>
<c:set var="sign_type" value="<%=EchoRunnerConstants.SIGN_TYPE%>"/>
<c:set var="google_sign" value="<%=EchoRunnerConstants.GOOGLE_SIGN%>"/>
<c:set var="keystore_pass" value="<%=EchoRunnerConstants.KEYSTORE_PASS%>"/>
<c:set var="keystore_alias" value="<%=EchoRunnerConstants.KEYSTORE_ALIAS%>"/>
<c:set var="key_pass" value="<%=EchoRunnerConstants.KEY_PASS%>"/>
<c:set var="keystore_file_location" value="<%=EchoRunnerConstants.KEYSTORE_FILE_LOCATION%>"/>
<c:set var="platform" value="<%=EchoRunnerConstants.PLATFORM%>"/>
<c:set var="build_logs" value="<%=EchoRunnerConstants.BUILD_LOGS%>"/>
<c:set var="team_id" value="<%=EchoRunnerConstants.TEAM_ID%>"/>
<c:set var="app_location" value="<%=EchoRunnerConstants.APP_LOCATION%>"/>
<c:set var="fusion_set" value="<%=EchoRunnerConstants.FUSION_SET%>"/>
<c:set var="provisioning_profile_file_location" value="<%=EchoRunnerConstants.PROVISIONING_FILE_LOCATION%>"/>
<c:set var="cert_file_location" value="<%=EchoRunnerConstants.CERT_FILE_LOCATION%>"/>
<c:set var="cert_pass" value="<%=EchoRunnerConstants.CERT_PASS%>"/>
<c:set var="ent_file_location" value="<%=EchoRunnerConstants.ENT_FILE_LOCATION%>"/>
<c:set var="build_to_test" value="<%=EchoRunnerConstants.BUILD_TO_TEST%>"/>
<c:set var="secondary_output" value="<%=EchoRunnerConstants.SECONDARY_OUTPUT%>"/>
<c:set var="output_file_name" value="<%=EchoRunnerConstants.OUTPUT_FILE_NAME%>"/>
<c:set var="app_id" value="<%=EchoRunnerConstants.APP_ID%>"/>

<l:settingsGroup title="Appdome Build2secure Settings">
<tr id="plaform">
<th><label for="plaform">Platform:</label></th>
<td>
  <props:selectProperty name="${platform}" onchange="EchoRunner.onChange()" enableFilter="true" className="smallField">
      <props:option value="Android" currValue="${platformSelected}">Android</props:option>
      <props:option value="iOS" currValue="${platformSelected}">iOS</props:option>
  </props:selectProperty>
</td>
</tr>

<tr id="app_location">
       <th><label for="${app_location}">App File Location: </label></th>
       <td>
               <props:textProperty name="${app_location}" size="56" maxlength="2000"/>
               <span class="error" id="error_${app_location}"></span>
       </td>
</tr>
<tr id="output_file_name">
 <th><label for="${output_file_name}">Output File Name (with no file ext.):</label></th>
 <td>
      <props:textProperty name="${output_file_name}" size="56" maxlength="100"/>
      <span class="error" id="error_${output_file_name}"></span>
 </td>
</tr>
<tr id="app_id">
       <th><label for="${app_id}">Firebase App ID: </label></th>
       <td>
               <props:textProperty name="${app_id}" size="56" maxlength="2000"/>
               <span class="error" id="error_${app_id}"></span>
       </td>
</tr>
<tr id="fusion_set">
 <th><label for="${fusion_set}">Fusion Set:</label></th>
 <td>
    <props:textProperty name="${fusion_set}" size="56" maxlength="100"/>
    <span class="error" id="error_${fusion_set}"></span>
 </td>
</tr>
<tr id="team_id">
 <th><label for="${team_id}">Team ID:</label></th>
 <td>
    <props:textProperty name="${team_id}" size="56" maxlength="100"/>
    <span class="error" id="error_${team_id}"></span>
 </td>
</tr>
<tr id="sign_typexx">
<th><label for="sign_type">Sign Type:</label></th>
<td>
  <props:selectProperty name="${sign_type}" onchange="EchoRunner.onChange()" enableFilter="true" className="mediumField">
    <props:option value="On-Appdome-Sign" currValue="${sign_typeSelected}">On-Appdome Sign</props:option>
    <props:option value="Private-Sign" currValue="${sign_typeSelected}">Private Sign</props:option>
    <props:option value="Auto-Dev-Sign" currValue="${sign_typeSelected}">Auto-Dev Sign</props:option>
  </props:selectProperty>
</td>
</tr>
<tr id="keystore_file_location">
<th><label for="${keystore_file_location}">Keystore File:</label></th>
<td>
    <div class="pos3">
        <props:textProperty name="${keystore_file_location}" size="56" maxlength="2000"/>
        <span class="error" id="error_${keystore_file_location}"></span>
    </div>
</td>
</tr>
<tr id="keystore_pass">
<th><label for="${keystore_pass}">Keystore Password:</label></th>
<td>
  <div class="pos3">
      <props:textProperty name="${keystore_pass}" size="56" maxlength="100"/>
      <span class="error" id="error_${keystore_pass}"></span>
  </div>
</td>
</tr>
<tr id="keystore_alias">
<th><label for="${keystore_alias}">Keystore Alias:</label></th>
<td>
    <div class="pos3">
        <props:textProperty name="${keystore_alias}" size="56" maxlength="100"/>
        <span class="error" id="error_${keystore_alias}"></span>
    </div>
</td>
</tr>
<tr id="key_pass">
<th><label for="${key_pass}">Key Password:</label></th>
<td>
  <div class="pos3">
      <props:textProperty name="${key_pass}" size="56" maxlength="100"/>
      <span class="error" id="error_${key_pass}"></span>
  </div>
</td>
</tr>
<tr id="google_sign">
  <th><label for="google_sign">Google Sign:</label></th>
  <td>
      <div class="pos4">
          <props:checkboxProperty name="${google_sign}"/>
      </div>
  </td>
</tr>
<tr id="google_play_fp">
  <th><label for="${google_play_fingerprint}">Google Play Fingerprint:</label></th>
  <td>
      <div class="pos5">
          <props:textProperty name="${google_play_fingerprint}" size="56" maxlength="100"/>
          <span class="error" id="error_${google_play_fingerprint}"></span>
      </div>
  </td>
</tr>
<tr id="fingerprint">
<th><label for="${fingerprint}">Fingerprint:</label></th>
<td>
    <props:textProperty name="${fingerprint}" size="56" maxlength="100"/>
    <span class="error" id="error_${fingerprint}"></span>
</td>
</tr>
<tr id="secondary_output">
  <th><label for="secondary_output">Secondary Output:</label></th>
  <td>
      <div class="pos4">
          <props:checkboxProperty name="${secondary_output}"/>
      </div>
  </td>
</tr>
<tr id="provisioning_profile_file_location">
<th><label for="${provisioning_profile_file_location}">Provisioning Profile Files:</label></th>
<td>
  <div class="pos3">
      <props:textProperty name="${provisioning_profile_file_location}" size="56" maxlength="2000"/>
      <span class="error" id="error_${provisioning_profile_file_location}"></span>
  </div>
</td>
</tr>
<tr id="cert_file_location">
<th><label for="${cert_file_location}">Signing Certificate File:</label></th>
<td>
    <div class="pos3">
        <props:textProperty name="${cert_file_location}" size="56" maxlength="2000"/>
        <span class="error" id="error_${cert_file_location}"></span>
    </div>
</td>
</tr>
<tr id="cert_pass">
  <th><label for="${cert_pass}">Certificate Password:</label></th>
  <td>
    <div class="pos3">
        <props:textProperty name="${cert_pass}" size="56" maxlength="100"/>
        <span class="error" id="error_${cert_pass}"></span>
    </div>
  </td>
</tr>
<tr id="ent_file_location">
  <th><label for="${ent_file_location}">Entitlement Files:</label></th>
  <td>
      <div class="pos3">
          <props:textProperty name="${ent_file_location}" size="56" maxlength="2000"/>
          <span class="error" id="error_${ent_file_location}"></span>
      </div>
  </td>
</tr>
<tr id="build_2_test">
<th><label for="build_2_test">Build to Test:</label></th>
<td>
  <props:selectProperty name="${build_to_test}" enableFilter="true" className="mediumField">
    <option value="None" currValue="${build_to_testSelected}">None</option>
    <option value="AWS_device_farm" currValue="${build_to_testSelected}">AWS device farm</option>
    <option value="Bitbar" currValue="${build_to_testSelected}">Bitbar</option>
    <option value="Browserstack" currValue="${build_to_testSelected}">Browserstack</option>
    <option value="Firebase" currValue="${build_to_testSelected}">Firebase</option>
    <option value="Katalon" currValue="${build_to_testSelected}">Katalon</option>
    <option value="Kobiton" currValue="${build_to_testSelected}">Kobiton</option>
    <option value="Lambdatest" currValue="${build_to_testSelected}">Lambdatest</option>
    <option value="Perfecto" currValue="${build_to_testSelected}">Perfecto</option>
    <option value="Saucelabs" currValue="${build_to_testSelected}">Saucelabs</option>
    <option value="Tosca" currValue="${build_to_testSelected}">Tosca</option>
  </props:selectProperty>
</td>
</tr>
<tr id="build_logs">
  <th><label for="build_logs">Build with Logs:</label></th>
  <td>
      <div class="pos4">
          <props:checkboxProperty name="${build_logs}"/>
      </div>
  </td>
</tr>

</l:settingsGroup>

<script type="text/javascript">
  EchoRunner = {
   onChange:function () {
     var sel = sign_type;
     var selectedValue = sel[sel.selectedIndex].value;
     var selplat = platform;
     var selectedPlatValue = selplat[selplat.selectedIndex].value;

     if (selectedPlatValue == 'Android') {
         BS.Util.show('app_id');
         BS.Util.show('fingerprint');
         BS.Util.show('google_play_fp');
         BS.Util.show('google_sign');
         BS.Util.hide('secondary_output');
         BS.Util.hide('provisioning_profile_file_location');
         BS.Util.hide('ent_file_location');
         BS.Util.hide('cert_file_location');
         BS.Util.hide('cert_pass');

         if ('On-Appdome-Sign' == selectedValue) {
             BS.Util.show('keystore_file_location');
             BS.Util.show('keystore_pass');
             BS.Util.show('keystore_alias');
             BS.Util.show('key_pass');
             BS.Util.show('secondary_output');
             BS.Util.hide('fingerprint');
         } else {
             if ('Auto-Dev-Sign' == selectedValue) {
                BS.Util.hide('secondary_output');
             } else {
                BS.Util.show('secondary_output');
             }
             BS.Util.hide('key_pass');
             BS.Util.hide('keystore_pass');
             BS.Util.hide('keystore_alias');
             BS.Util.hide('keystore_file_location');
         }

     } else {
        BS.Util.hide('app_id');
        BS.Util.hide('fingerprint');
        BS.Util.hide('google_play_fp');
        BS.Util.hide('google_sign');
        BS.Util.hide('secondary_output');
        BS.Util.hide('key_pass');
        BS.Util.hide('keystore_pass');
        BS.Util.hide('keystore_alias');
        BS.Util.hide('keystore_file_location');
        BS.Util.show('provisioning_profile_file_location');
        BS.Util.show('ent_file_location');
        if ('On-Appdome-Sign' == selectedValue) {
            BS.Util.show('cert_file_location');
            BS.Util.show('cert_pass');
        } else {
            BS.Util.hide('cert_file_location');
            BS.Util.hide('cert_pass');
        }
     }

   }
  };
  EchoRunner.onChange();
</script>