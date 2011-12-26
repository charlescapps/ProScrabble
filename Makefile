ALL_SRC = src/capps/scrabble/*.java 
CMD1 = javac
OPT1 = -g -d bin
CMD2 = jar
OPT2 = cvef
CHG_DIR = -C bin
DEST = .
PKG = capps.scrabble
MAIN_CLASS = capps.scrabble.Main

scrabble.jar: $(ALL_SRC)
	$(CMD1) $(OPT1) $(ALL_SRC)
	$(CMD2) $(OPT2) $(MAIN_CLASS) scrabble.jar $(CHG_DIR) $(DEST)

gendict.jar: $(ALL_SRC)
	$(CMD1) $(OPT1) $(ALL_SRC)
	$(CMD2) $(OPT2) capps.scrabble.GenDict gendict.jar $(CHG_DIR) $(DEST)

clean: 
	rm -f scrabble.jar
	rm -f gendict.jar
