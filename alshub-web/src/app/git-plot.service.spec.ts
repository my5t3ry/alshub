import { TestBed } from '@angular/core/testing';

import { GitPlotService } from './git-plot.service';

describe('GitPlotService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: GitPlotService = TestBed.get(GitPlotService);
    expect(service).toBeTruthy();
  });
});
