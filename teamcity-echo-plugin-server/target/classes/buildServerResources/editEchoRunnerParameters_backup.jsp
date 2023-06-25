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
<c:set var="app_location" value="<%=EchoRunnerConstants.APP_LOCATION%>"/>
<c:set var="fusion_set" value="<%=EchoRunnerConstants.FUSION_SET%>"/>

<l:settingsGroup title="Appdome Build2secure Settings">

  <tr id="app_location" style="display: initial">
      <th><label for="${app_location}">App Location URL: <l:star/></label></th>
      <td>
          <div class="pos3">
              <props:textProperty name="${app_location}" size="56" maxlength="2000"/>
              <span class="error" id="error_${app_location}"></span>
          </div>
      </td>
    </tr>
  <th><label for="plaform">Platform: </label></th>
  <td>
      <props:selectProperty name="${platform}" onchange="BS.EchoRunner.onPlatformChanged()" enableFilter="true" className="smallField">
          <props:option value="android" currValue="${platformSelected}">Android</props:option>
          <props:option value="ios" currValue="${platformSelected}">iOS</props:option>
      </props:selectProperty>
  </td>
  <tr></tr>
  <tr id="fusion_set" style="display: initial">
      <th><label for="${fusion_set}">Fusion Set: <l:star/></label></th>
      <td>
          <div class="pos3">
              <props:textProperty name="${fusion_set}" size="56" maxlength="100"/>
              <span class="error" id="error_${fusion_set}"></span>
          </div>
      </td>
  </tr>
  <th><label for="sign_type">Sign Type: </label></th>
  <td>
        <props:selectProperty name="${sign_type}" onchange="BS.EchoRunner.onSignChanged()" enableFilter="true" className="mediumField">
          <option value="on-appdome-sign" currValue="${sign_typeSelected}">On-Appdome Sign</option>
          <option value="private-sign" currValue="${sign_typeSelected}">Private Sign</option>
          <option value="auto-dev-sign" currValue="${sign_typeSelected}">Auto-Dev Sign</option>
        </props:selectProperty>
  </td>
  <tr id="googe_sign" style="display: initial">
    <th><label for="google_sign">Google Sign: </label></th>
    <td>
        <div class="pos4">
            <props:checkboxProperty name="${google_sign}"/>
        </div>
    </td>
  </tr>
  <tr id="fingerprint" style="display: initial">
    <th><label for="${fingerprint}">Fingerprint: </label></th>
    <td>
        <div class="pos3">
            <props:textProperty name="${fingerprint}" size="56" maxlength="100"/>
            <span class="error" id="error_${fingerprint}"></span>
        </div>
    </td>
  </tr>
  <tr id="google_play_fp" style="display: initial">
    <th><label for="${google_play_fingerprint}">Google Play Fingerprint: </label></th>
    <td>
        <div class="pos5">
            <props:textProperty name="${google_play_fingerprint}" size="56" maxlength="100"/>
            <span class="error" id="error_${google_play_fingerprint}"></span>
        </div>
    </td>
  </tr>

  <tr id="keystore_file_location" style="display: initial">
      <th><label for="${keystore_file_location}">Keystore File Location: </label></th>
      <td>
          <div class="pos3">
              <props:textProperty name="${keystore_file_location}" size="56" maxlength="2000"/>
              <span class="error" id="error_${keystore_file_location}"></span>
          </div>
      </td>
  </tr>
  <tr id="keystore_pass" style="display: initial">
        <th><label for="${keystore_pass}">Keystore Password: </label></th>
        <td>
            <div class="pos3">
                <props:textProperty name="${keystore_pass}" size="56" maxlength="100"/>
                <span class="error" id="error_${keystore_pass}"></span>
            </div>
        </td>
    </tr>
  <tr id="keystore_alias" style="display: initial">
      <th><label for="${keystore_alias}">Keystore Alias: </label></th>
      <td>
          <div class="pos3">
              <props:textProperty name="${keystore_alias}" size="56" maxlength="100"/>
              <span class="error" id="error_${keystore_alias}"></span>
          </div>
      </td>
  </tr>
  <tr id="key_pass" style="display: initial">
    <th><label for="${key_pass}">Key Password: </label></th>
    <td>
        <div class="pos3">
            <props:textProperty name="${key_pass}" size="56" maxlength="100"/>
            <span class="error" id="error_${key_pass}"></span>
        </div>
    </td>
</tr>


  <tr id="build_logs" style="display: initial">
      <th><label for="build_logs">Build with logs: </label></th>
      <td>
          <div class="pos4">
              <props:checkboxProperty name="${build_logs}"/>
          </div>
      </td>
    </tr>

</l:settingsGroup>

<script type="text/javascript">
    BS.EchoRunner = {
    onPlatformChanged:function () {
      var sel = $('${platform}');
      var selectedValue = sel[sel.selectedIndex].value;
      if ('android' == selectedValue) {
        BS.Util.show('google_play_fp');
        BS.Util.show('fingerprint');
        BS.Util.show('googe_sign');
      } else {
        BS.Util.hide('google_play_fp');
        BS.Util.hide('fingerprint');
        BS.Util.hide('googe_sign');
    }
    BS.VisibilityHandlers.updateVisibility('mainContent');
  };
  BS.EchoRunner.onPlatformChanged();


  BS.EchoRunner = {
    onSignChanged:function () {
      var sel = $('${sign_type}');
      var selectedValue = sel[sel.selectedIndex].value;
    }
  };
  BS.EchoRunner.onSignChanged();

</script>