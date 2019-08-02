import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectGitGraphComponent } from './project-git-graph.component';

describe('ProjectGitGraphComponent', () => {
  let component: ProjectGitGraphComponent;
  let fixture: ComponentFixture<ProjectGitGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectGitGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectGitGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
