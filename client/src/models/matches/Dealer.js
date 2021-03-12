import Player from './Player';
class Dealer extends Player {
    constructor(fetchedDealer) {
        super(fetchedDealer);
        this.numOfBusts = fetchedDealer.numOfBusts;
    }
}


export default Dealer;