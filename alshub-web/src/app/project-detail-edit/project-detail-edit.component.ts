import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {NotifierService} from "angular-notifier";
import {Observable} from "rxjs";
import {RequestInterceptorService} from "../../request-interceptor.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-project-detail-edit',
  templateUrl: './project-detail-edit.component.html',
  styleUrls: ['./project-detail-edit.component.scss']
})
export class ProjectDetailEditComponent implements OnInit {
  projectId: any;
  endpoint = 'http://localhost:8090/api/project';

  project: any;
  changes: any;
  public projectForm: FormGroup;

  constructor(private http: HttpClient, private route: ActivatedRoute, private notifierService: NotifierService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.projectId = params['projectId'];
      this.receiveProject(this.projectId).subscribe(data => {
        this.project = data;
        this.projectForm = new FormGroup({
          name: new FormControl('', Validators.required),
          devices: new FormControl(''),
          genres: new FormControl(''),
        });
        this.projectForm.patchValue(this.project);
        this.projectForm.patchValue({devices: this.project.abletonProject.externalDevices});
      });
      // this.fetchChanges();
    });

  }

  receiveProject(id): Observable<any> {
    return this.http.get<any>(this.endpoint + "/" + id, RequestInterceptorService.httpOptions).pipe();
  }

  save() {
    console.log(this.projectForm);
  }
}
