all: run

clean:
	rm -f out/Main.jar out/RabinKarp.jar

out/Main.jar: out/parcs.jar src/Main.java src/Task.java src/Result.java src/PatternResult.java
	@javac -cp out/parcs.jar src/Main.java src/Task.java src/Result.java src/PatternResult.java
	@jar cf out/Main.jar \
		-C src Main.class \
		-C src Task.class \
		-C src Result.class \
		-C src PatternResult.class
	@rm -f src/Main.class src/Task.class src/Result.class src/PatternResult.class

out/RabinKarp.jar: out/parcs.jar src/RabinKarp.java src/Task.java src/Result.java src/PatternResult.java
	@javac -cp out/parcs.jar src/RabinKarp.java src/Task.java src/Result.java src/PatternResult.java
	@jar cf out/RabinKarp.jar \
		-C src RabinKarp.class \
		-C src Task.class \
		-C src Result.class \
		-C src PatternResult.class
	@rm -f src/RabinKarp.class src/Task.class src/Result.class src/PatternResult.class

build: out/Main.jar out/RabinKarp.jar

run: build
	@cd out && java -cp 'parcs.jar:Main.jar' Main input1.txt 2
