const fs = require('fs');
const path = require('path');
const filePath = path.join(__dirname, './trace_13_114944.json');
const data = fs.readFileSync(filePath, 'utf-8');

const waits = data.split(/\r?\n/)
    .filter(line => line.length > 0)
    .filter(line => line.includes("WAIT"))
    .map(line => JSON.parse(line.substring(0, line.length - 1)));
const drives = data.split(/\r?\n/)
    .filter(line => line.length > 0)
    .filter(line => line.includes("DRIVE"))
    .map(line => JSON.parse(line.substring(0, line.length - 1)));
const loads = data.split(/\r?\n/)
    .filter(line => line.length > 0)
    .filter(line => line.includes("LOAD"))
    .map(line => JSON.parse(line.substring(0, line.length - 1)));



// let roads = new Object()

// loads.forEach(load => {
//   const drive = drives
//     .filter(drive => drive.pid === load.pid && drive.tid === load.tid && drive.ph === load.ph)
//     .filter(drive => drive.args != null)
//     .filter(drive => drive.ts < load.ts)
//     .sort((a, b) => b.ts - a.ts)[0];

//   const wait = waits
//     .filter(wait => wait.pid === load.pid && wait.tid === load.tid && wait.ph === load.ph)
//     .filter(wait => drive.ts < wait.ts || wait.ts < load.ts)
//     .sort((a, b) => b.ts - a.ts)[0];
// })


waits.forEach(wait => {
  const drive = drives
    .filter(drive => drive.pid === wait.pid && drive.tid === wait.tid && drive.ph === wait.ph)
    .filter(drive => drive.args != null)
    .filter(drive => drive.ts < wait.ts)
    .sort((a, b) => b.ts - a.ts)[0];
  const load = loads
    .filter(load => load.pid === wait.pid && load.tid === wait.tid && load.ph === wait.ph)
    .filter(drive => drive.ts > wait.ts)
    .sort((a, b) => a.ts - b.ts)[0];

  if (drive == null || load == null)
    return

  //console.log(wait)
  //console.log(drive)
  //console.log(load)
  //console.log("\n")
  //console.log("\n")

  console.log(drive.args.b + "\nclosed at: " + (wait.ts / 1000) % 24 + "\nopens at: " + (load.ts / 1000) % 24 + "\n\n")
})
