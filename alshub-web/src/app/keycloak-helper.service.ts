import {Injectable} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class KeycloakHelperService {

  constructor(private keycloak: KeycloakService) {
  }

  getUserDetails(): any {
    const userDetails = this.keycloak.getKeycloakInstance().tokenParsed['userDetails'];
    return  userDetails;
  }

  loadProfile(): Promise<any> {
    return this.keycloak.loadUserProfile();
  }

  logout(): void {
    this.keycloak.logout();
  }

}
