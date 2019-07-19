import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectCardMetaDataComponent } from './project-card-meta-data.component';

describe('ProjectCardMetaDataComponent', () => {
  let component: ProjectCardMetaDataComponent;
  let fixture: ComponentFixture<ProjectCardMetaDataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectCardMetaDataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectCardMetaDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
