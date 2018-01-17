all: 
	javac -d build/classes src/rpweb/*

run: 
	java -cp build/classes rpweb.RPWeb
