rmdir /s /q release
rmdir /s /q "C Sharp/Object Validator/bin"
dotnet pack "C Sharp/Object Validator"
dotnet nuget locals all --clear
dotnet test "C Sharp/Object Validator Test"
call "Java/Object Validator/gradlew" -p "Java/Object Validator" clean
call "Java/Object Validator/gradlew" -p "Java/Object Validator" test
call "Java/Object Validator/gradlew" -p "Java/Object Validator" build
cd "TypeScript/Object Validator"
call npm i
call npm run prepublishOnly
cd ../..
robocopy "C Sharp/Object Validator/bin/Release" release *.nupkg
robocopy "Java/Object Validator/lib/build/libs" release *.?.jar
robocopy "TypeScript/Object Validator/dist" release/@quicksilver0218/object-validator/dist
robocopy "TypeScript/Object Validator" release/@quicksilver0218/object-validator package.json