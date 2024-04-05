const Web3 = require('web3');
const contractABI = require('./YourContractABI.json'); // Replace with your contract's ABI
const contractAddress = '0xYourContractAddress'; // Replace with your contract's address
const web3 = new Web3('http://localhost:8545'); // Connect to your Ethereum node

const contract = new web3.eth.Contract(contractABI, contractAddress);

async function sendDataToContract(data) {
    const accounts = await web3.eth.getAccounts();
    const account = accounts[0]; // Assuming you want to use the first account for the transaction

    // Call your smart contract function here, passing the data as parameters
    await contract.methods.yourFunctionName(data).send({ from: account });
}

// Example usage
const dataFromDatabase = "0305830510000111000090000000103000001000000000001110011100800000";
sendDataToContract(dataFromDatabase)
    .then(() => console.log("Data sent to contract"))
    .catch(error => console.error("Error sending data:", error));
