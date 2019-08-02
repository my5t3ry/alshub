import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
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
  private _pictureId: string;

  get pictureData(): string {
    return this._pictureData;
  }

  projectId: any;
  endpoint = 'http://localhost/api/project';

  project: any;
  changes: any;
  public projectForm: FormGroup;
  private _pictureData: string = null;


  constructor(private http: HttpClient, private route: ActivatedRoute,private router: Router) {
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
          description: new FormControl(''),
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
    this.project.name = this.projectForm.controls['name'].value;
    this.project.abletonProject.externalDevices = this.projectForm.controls['devices'].value;
    this.project.genres = this.projectForm.controls['genres'].value;
    this.project.pictureId = this._pictureId;
    this.http.post(this.endpoint + '/edit-project/', this.project)
      .subscribe(
        data => {
          // @ts-ignore
          this.router.navigateByUrl("/project-detail/" + data.id)
        });
  }

  pictureSelectHandler(data: Element) {
    this._pictureData = data.attributes.getNamedItem('src').value;
    this._pictureId = data.attributes.getNamedItem('data-id').value;
  }

  initReset() {
    this._pictureData = null;
  }
}
