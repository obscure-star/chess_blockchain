-- add columns to table
CREATE TABLE CHESS_DATA (
                            ID SERIAL PRIMARY KEY,
                            GAME_ID VARCHAR(36),
                            ROUND INT,
                            BOARD_REPRESENTATION VARCHAR(64),
                            BOARD_REPRESENTATION_INT INT,
                            PIECE_COUNT JSON,
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
                            WINNER INT,
                            NEXT_MOVE VARCHAR(36),
                            NEXT_MOVE_INDEX INT,
                            LENGTH_LEGAL_MOVES INT,
                            LEGAL_MOVES JSON
);

DROP TABLE CHESS_DATA;


