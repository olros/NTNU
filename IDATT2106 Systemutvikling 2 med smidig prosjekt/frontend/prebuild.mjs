import { exec } from 'child_process';
import { promisify } from 'util';
import { writeFile } from 'fs/promises';

// attempt to find an existing deploy for the given branch
const getDeployUrl = async (branch) => {
  const { stdout } = await promisify(exec)(`firebase hosting:channel:list --json`);
  const { result } = JSON.parse(stdout);

  return result.channels.find((deploy) => deploy.name.split('/').pop() === branch)?.url;
};

// create a new hosting preview channel without deploying and return its' url
const generateBranchDeployUrl = async (branch) => {
  const { stdout } = await promisify(exec)(`firebase hosting:channel:create ${branch} --json`);
  const { result } = JSON.parse(stdout);

  return result.url;
};

// set the preview url in the CI env so future stages can use it
const setEnv = async (url) => {
  const envs = {
    MY_DEPLOYED_URL: url,
  };

  const stringified = Object.entries(envs).reduce((prev, [key, value]) => `${prev}${key}=${value}\n`, '');

  await writeFile('build.env', stringified);
};

// ensure that there is a preview channel ready for our deploy, and put its' url in the env
const prepare = async (branch) => {
  let url = await getDeployUrl(branch);

  if (!url) {
    url = await generateBranchDeployUrl(branch);
  }

  await setEnv(url);

  console.log(`preparation complete: ${url}`);
};

prepare(process.argv[2] || process.env.CI_COMMIT_REF_SLUG).catch((e) => {
  console.error(e);

  process.exit(1);
});
