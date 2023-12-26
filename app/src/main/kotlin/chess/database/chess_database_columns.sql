-- add columns to table
CREATE TABLE CHESS_DATA (
                            ROUND_ID VARCHAR(36) PRIMARY KEY,
                            GAME_ID VARCHAR(36),
                            ROUND INT,
                            BOARD_REPRESENTATION VARCHAR(64),
                            BOARD_REPRESENTATION_INT INT,
                            PIECE_COUNT JSON,
                            LEGAL_MOVES JSON,
                            THREATS_AND_ATTACKS JSON,
                            PIECE_ACTIVITY JSON,
                            KING_SAFETY JSON,
                            PAWN_STRUCTURE JSON,
                            MATERIAL_BALANCE JSON,
                            CENTER_CONTROL JSON,
                            PREVIOUS_MOVES JSON,
                            MOVE VARCHAR(36),
                            WHITE_WINS BOOLEAN,
                            BLACK_WINS BOOLEAN,
                            WINNER INT
);

DROP TABLE CHESS_DATA;


