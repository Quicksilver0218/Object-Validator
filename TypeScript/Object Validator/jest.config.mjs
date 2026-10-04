/** @type {import("ts-jest").JestConfigWithTsJest} */
const isDist = process.env.TEST_TARGET === "dist";

export default {
  transform: {
    "^.+\\.tsx?$": [
      "ts-jest",
      {
        useESM: true,
        tsconfig: "./tsconfig.json",
      },
    ],
  },
  moduleNameMapper: {
    "^(.*)/src((?:/.*)?)$": process.env.TEST_TARGET === "dist" ? "$1/dist$2" : "$1/src$2",
  },
};