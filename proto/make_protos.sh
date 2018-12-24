#!/bin/bash
cd ../
rootProject=`pwd`

protoPath=${rootProject}/proto/
wire_compiler=${rootProject}/proto/wire-compiler-2.3.0-RC1-jar-with-dependencies.jar
modelPath=${rootProject}/retrofit/src/main/java/

java -jar ${wire_compiler} --proto_path=${protoPath} --java_out=${modelPath} $1

echo "Done"
