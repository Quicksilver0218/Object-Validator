export default class ValidationFailure {
    readonly id?: number;
    readonly message?: string;

    constructor(id?: number, message?: string) {
        this.id = id;
        this.message = message;
    }
}