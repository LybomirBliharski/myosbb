import {Injectable} from "@angular/core";
import {Http, Headers} from "@angular/http";
import "rxjs/add/operator/map";
import "rxjs/add/operator/toPromise";
import {IRole} from "./role";
import {Observable} from "rxjs/Observable";
import ApiService = require("../../../../shared/services/api.service");

@Injectable()
export class RoleService { 

    private deleteUrl:string = ApiService.serverUrl + '/restful/role/id';
    private postUrl:string = ApiService.serverUrl + '/restful/role/';
    private putUrl:string = ApiService.serverUrl + '/restful/role/';
    private getUrl:string = ApiService.serverUrl + '/restful/role?pageNumber=';
    

    constructor(private http: Http) { }

    //Checked
    getAllRole (pageNumber:number):Observable<any> {
        console.log('Get all role');
        return this.http.get(this.getUrl + pageNumber)
            .map((response)=> response.json())
            .catch((error)=>Observable.throw(error));
    }
    //Checked
    addRole(role:IRole) {
        console.log('Add new role');
        return this.http.post(this.postUrl, JSON.stringify(role))
                        .toPromise()
                        .then(res => res.json())
                        .catch((error)=>console.error(error));
    }
    //Checked
    editRole(role:IRole)  {
        console.log('Edit role');
        return this.http.put(this.putUrl, JSON.stringify(role))
                        .toPromise()
                        .then(res => res.json())
                        .catch(this.handleError);

    }

    //Checked
    deleteRole(role: IRole){
        let url = ` ${this.deleteUrl}/${role.roleId}`;
        console.log('delete role by id: ' + role.roleId);
            return this.http.delete(url)
                        .toPromise().then(res => role)
                        .catch((error)=>console.error(error));
    }

    getAllRolesSorted(pageNumber:number, name:string, order:boolean):Observable<any> {
        return this.http.get(this.getUrl + pageNumber + '&&sortedBy=' + name + '&&asc=' + order)
            .map((response)=> response.json())
            .catch((error)=>Observable.throw(error));
    }
    private handleError(error: any):Promise<any> {
        console.log('HandleError', error);
        return Promise.reject(error.message || error);
    }
}