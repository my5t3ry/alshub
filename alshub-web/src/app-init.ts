import {KeycloakService} from 'keycloak-angular';
export function initializer(keycloak: KeycloakService): () => Promise<any> {
  return (): Promise<any> => keycloak.init({
    initOptions: {
      onLoad: 'login-required',
      checkLoginIframe: true
    }
  });
}
