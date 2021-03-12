class User {
    constructor(fetchedUser) {
        this.name = fetchedUser.name;
        this.email = fetchedUser.email;
        this.money = fetchedUser.money;
        this.id = fetchedUser.id;
    }
}


export default User;