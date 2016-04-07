/*
 * Copyright (c) 2012 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniVote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 *
 */


(function (window) {

	function Config() {

		/**
		 * The url of the public board.
		 */
		this.URL_UNIBOARD_GET = '/univote-board-rest/messages/query';
		this.URL_UNIBOARD_POST = '/univote-board-rest/messages/post';


		/**
		 * The default base for BigInteger representations as string.
		 */
		this.BASE = 10;

		/**
		 * The hash function used all over the client.
		 */
		this.HASH_FUNCTION = CryptoJS.SHA256; // CryptoJS.SHA512;

		/**
		 * UniVote Board setting: pk, p, q and g for Schnorr signature.
		 */
		this.BOARD_SETTING = {
			P: "18111848663142005571178770624881214696591339256823507023544605891411707081617152319519180201250440615163700426054396403795303435564101919053459832890139496933938670005799610981765220283775567361483662648340339405220348871308593627647076689407931875483406244310337925809427432681864623551598136302441690546585427193224254314088256212718983105131138772434658820375111735710449331518776858786793875865418124429269409118756812841019074631004956409706877081612616347900606555802111224022921017725537417047242635829949739109274666495826205002104010355456981211025738812433088757102520562459649777989718122219159982614304359",
			Q: "19689526866605154788513693571065914024068069442724893395618704484701",
			G: "2859278237642201956931085611015389087970918161297522023542900348087718063098423976428252369340967506010054236052095950169272612831491902295835660747775572934757474194739347115870723217560530672532404847508798651915566434553729839971841903983916294692452760249019857108409189016993380919900231322610083060784269299257074905043636029708121288037909739559605347853174853410208334242027740275688698461842637641566056165699733710043802697192696426360843173620679214131951400148855611740858610821913573088059404459364892373027492936037789337011875710759208498486908611261954026964574111219599568903257472567764789616958430",
			PK: "11075949341840480120182305361035803263711518941450827975945116019039093528237323515312626308615357380504451281190947205021123022514020934341264968225353781451113372243134470653473040701998424169672246044791632887766068736388524375601205179281952692431042694923525547700173968102721829984674502970462192964991332705472481751501472789139483271553184423587366471657303764322871507106229527574564570792744189552910263752167390047703707487771319638919267418224368944109600255979919699831602114666856840345379496581464817276430509598200375681712165288950901015764729219949028273721681144058001600819787798577842963728037398"
		};

		/**
		 * Signature setting of known authors of posts. If the author is not available here, his/her signature isnt validated.
		 * Requries pk, p, q and g for Schnorr signature.
		 */
		this.KNOWN_AUTHORS = [{
				P: "16158503035655503650357438344334975980222051334857742016065172713762327569433945446598600705761456731844358980460949009747059779575245460547544076193224141560315438683650498045875098875194826053398028819192033784138396109321309878080919047169238085235290822926018152521443787945770532904303776199561965192760957166694834171210342487393282284747428088017663161029038902829665513096354230157075129296432088558362971801859230928678799175576150822952201848806616643615613562842355410104862578550863465661734839271290328348967522998634176499319107762601824041814772893165831522227453224035124084988448041816607879141260367",
				Q: "13479973333575319897333507543509815336818572211270286240551805124797",
				G: "1134269898971939660256221417602992673757781560247338745711142004292707499263615663726956407344787191388627204394785328286520316952457371640119709459567156265272656919807409699971484184844404437839488942735405277198676036283721356819677333140642790964300984664518053443525909642640603162099914341539824434934715022408665363634880726847516892393401614383985819689883140616831792350484976314212608052796942951089533668814348814636566690462232705866142760699021764820760170288154471669270258911504614068561280584855398438862525973273228514639148263645084849683718631964199688562411013834474496797602932228487527202996447",
				PK: "593185205926337023364954498156940157008335316215310395294064818685001942728075323622144970754132034085199757075076931643897337127005205360433599545593709504008415690804832364387571055427795424799103935020488249235330908859703642624618409112229119664971995997279071198169447717145621189791553161270573826957869988967239972535334425262285628539611982783957195232305311272609200526173294447377682440387293951293375165251517114248954071003825680896470633535729179150907633582598544761975190719116016235447591074468361301221550449683164102391420892221222375140981968476879858375471235361469444506718639507834728212868651"
			}];
	}
	window.ubConfig = new Config();

})(window);

