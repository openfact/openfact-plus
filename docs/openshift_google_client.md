### Setup Google client ID and secret

We now have Google integration which for now requires a manual OAuth setup to obtain a clientid and secret that we will give to keycloak. 

Follow these steps using the output of:
```
echo http://keycloak-clarksnut-system.$(minishift ip).nip.io/auth/realms/clarksnut/broker/google/endpoint
```

https://console.cloud.google.com/apis/credentials/oauthclient

![Register OAuth App](../images/register-oauth.png)


Once you have found your client ID and secret for the new fabric8 app on your github settings then type the following:

```
export GOOGLE_OAUTH_CLIENT_ID=TODO
export GOOGLE_OAUTH_CLIENT_SECRET=TODO
```

where the above `TODO` text is replaced by the actual client id and secret from your github settings page!
