class CanvasConstants {
    static SCALING_DENOMINATOR = 1350;
    static DEALER_X_OFFSET = 0;
    static DEALER_Y_OFFSET = -250;

    static Ps_1_6_POSITION_OFFSET_X = 420; //players 1 and 6
    static Ps_1_6_POSITION_OFFSET_Y = 0;
    static Ps_2_5_POSITION_OFFSET_X = 300;
    static Ps_2_5_POSITION_OFFSET_Y = 140;
    static Ps_3_4_POSITION_OFFSET_X = 110;
    static Ps_3_4_POSITION_OFFSET_Y = 225;

    static Ps_CARD_OFFSETS = [
        { x: -30, y: 80 }, // dealer
        { x: 0, y: -60 },
        { x: 0, y: -62 },
        { x: 0, y: -67 },
        { x: 0, y: -75 },
        { x: 0, y: -80 },
        { x: -40, y: -85 } // player 6
    ];

    static CARD_NUM_OFFSETS = [
        { x: 0, y: 0, angle: 0 },
        { x: 30, y: 0, angle: 20 },
        { x: 60, y: 10, angle: 30 },
        { x: 85, y: 25, angle: 40 },
    ];

    static DEALING_CARD_INITIAL_COORDS = { x: 500, y: 20 };
    static CARD_INTERVAL = 10;

}


export default CanvasConstants;