import {TestBed} from '@angular/core/testing';
import {KeycloakHelperService} from './keycloak-helper.service';

describe('KeycloakHelperService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));
  it('should be created', () => {
    const service: KeycloakHelperService = TestBed.get(KeycloakHelperService);
    expect(service).toBeTruthy();
  });
});
