all: run

clean:
	rm -f out/Main.jar out/RabinKarp.jar

out/Main.jar: out/parcs.jar src/Main.java src/Chunk.java src/Result.java
	@javac -cp out/parcs.jar src/Main.java src/Chunk.java src/Result.java
	@jar cf out/Main.jar -C src Main.class -C src Chunk.class -C src Result.class
	@rm -f src/Main.class src/Chunk.class src/Result.class

out/RabinKarp.jar: out/parcs.jar src/RabinKarp.java src/Chunk.java src/Result.java
	@javac -cp out/parcs.jar src/RabinKarp.java src/Chunk.java src/Result.java
	@jar cf out/RabinKarp.jar -C src RabinKarp.class -C src Chunk.class -C src Result.class
	@rm -f src/RabinKarp.class src/Chunk.class src/Result.class

build: out/Main.jar out/RabinKarp.jar

run: out/Main.jar out/RabinKarp.jar
	@cd out && java -cp 'parcs.jar:Main.jar' Main input 2
