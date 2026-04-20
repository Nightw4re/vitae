import { execFileSync } from 'node:child_process';

// VSCode Java extension sets JAVA_TOOL_OPTIONS which can cause bootstraplauncher
// to appear on both module-path and class-path simultaneously. Clear it before launch.
const env = { ...process.env };
delete env.JAVA_TOOL_OPTIONS;
delete env._JAVA_OPTIONS;
delete env.JDK_JAVA_OPTIONS;

const task = process.argv[2] ?? 'runClient';

execFileSync('cmd.exe', ['/c', 'gradlew.bat', task], { stdio: 'inherit', env });
