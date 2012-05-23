function onInitOk() {
	startDigitalSignature();
}

function onSignError(txt){
	alert("Error:  " + txt);
}

function Sign(downloadUrl, uploadUrl, lang, signatureOutputFormat, inputDataEncoding, outputDataEncoding) {
	var cp= document.getElementById("CryptoApplet");
	var loc= document.location.href;
	
	if (!cp){
	  alert("ERROR: getting the applet object from tag id CryptoApplet");
	  return; 
	}
	
	// Setting output RAW
	cp.setSignatureOutputFormat(signatureOutputFormat);
	cp.setLanguage(lang);
	cp.setInputDataEncoding(inputDataEncoding);
	
	// Setting pdf input / output
	cp.setOutputDataEncoding(outputDataEncoding);
	cp.signDataUrlToUrl(downloadUrl , uploadUrl, 'data');
}

function onSignOk(txt){ 
	destroyDigitalSignatureApplet();
}

function onWindowShow(){
	// Nothing to do
}

function onSignCancel() {
	destroyDigitalSignatureApplet();
}