ALL_SRC = src/capps/scrabble/*.java src/capps/scrabble/acm/*.java
CMD1 = javac
OPT1 = -g -d bin
CMD2 = jar
OPT2 = cvef
CHG_DIR = -C bin
DEST = .
MAIN_CLASS = capps.scrabble.Main
BEST_MAIN = capps.scrabble.acm.BestMoveMain
DECIDE_MAIN = capps.scrabble.acm.Arbitrate

scrabble.jar: $(ALL_SRC)
	$(CMD1) $(OPT1) $(ALL_SRC)
	$(CMD2) $(OPT2) $(MAIN_CLASS) scrabble.jar $(CHG_DIR) $(DEST)

bestmove.jar: $(ALL_SRC)
	$(CMD1) $(OPT1) $(ALL_SRC)
	$(CMD2) $(OPT2) $(BEST_MAIN) bestmove.jar $(CHG_DIR) $(DEST)

arbitrate.jar: $(ALL_SRC)
	$(CMD1) $(OPT1) $(ALL_SRC)
	$(CMD2) $(OPT2) $(DECIDE_MAIN) arbitrate.jar $(CHG_DIR) $(DEST)

clean: 
	rm -f scrabble.jar
	rm -f gendict.jar
