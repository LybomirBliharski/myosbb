import { User } from './User';
export interface IOsbb {
    osbbId: number;
    name: string;
    description: string;
    creator:User;
    address: string;
    district:string;
    creationDate: Date;
}

export class Osbb implements IOsbb {
    osbbId: number;
    name: string;
    description: string;
    creator: User;
    address: string;
    district:string;
    creationDate: Date;

    constructor( name:string, description:string, address: string, district: string) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.district = district;
    }
}