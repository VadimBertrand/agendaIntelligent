// à récupérer dès le début :
listeEvent1; 
listeEvent2;
listeEventCommun;
nomAgenda1;
nomAgenda2;
heureDebut;
// utilisées dans le code ensuite :
heureCourante;
nomEvent;
rowspan1;
rowspan2;
rowspan3;
lineNumber;
classValue;

// code HTML à écrire au début du fichier html :
"<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 5.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>" +

"<html>" +
	"<title>Votre agenda</title>" +
	"<head>" +
		"<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>" +
		"<meta name='title' content='Votre agenda'/>" +
		"<style>" +
			"body {" +
				"background-color:#FFFFFF;" +
				"text-align:center;" +
			"}" +
			"#tableId {" +
				"border-collapse: collapse;" +
				"width:400px;" +
				"height:550px;" +
			"}" +
			".tableBody {" +
				"height:12%;" +
				"border-left:1px solid black;" +
				"border-right:1px solid black;" +
			"}" +
			".hour {" +
				"width:1%;" +
				"vertical-align:bottom;" +
				"border-right:1px solid black;" +
			"}" +
			".event {" +
				"width:13%;" +
				"border-top:1px solid black;" +
				"border-bottom:1px solid black;" +
			"}" +
			".noEvent {" +
				"width:13%;" +
				"border:none;" +
				"background-color:#D0D0D0;" +
			"}" +
			".column1 {" +
				"border-right:1px solid black;" +
			"}" +
			".column2 {" +
				"border-right:1px solid black;" +
			"}" +
			".column3 {" +
				"border-right:1px solid black;" +
			"}" +
			"#tableHeader {" +
				"height:4%;" +
				"border-left:1px solid black;" +
				"border-right:1px solid black;" +
			"}" +
			"#tableHeader td {" +
				"border-top:1px solid black;" +
				"border-bottom:1px solid black;" +
			"}" +
			"#tableHeader .hour {" +
				"border-top:none;" +
				"border-bottom:none;" +
			"}" +
			"#line1 td {" +
				"border-top:none;" +
			"}" +
			"#line8 td {" +
				"border-bottom:1px solid black;" +
			"}" +
			"#line8 .hour {" +
				"border-bottom:none;" +
			"}" +
		"</style>" +
	"</head>" +

	"<body>" +
		"<table id='tableId' cellspacing="0" cellpadding="0">" +
			"<tr id='tableHeader'>" +
				"<td class='hour'>8h</td>"
				
// écrire le noms des agendas :
				"<td class='column1'>" + nomAgenda1 +"</td>" +
				"<td class='column2'>" + nomAgenda2 +"</td>" +
				"<td class='column3'>Commun</td>" +
			"</tr>"
				
heureCourante = heureDebut;
// 	pour chaque créneau :
	for lineNumber = 1 ; lineNumber == 8 ; lineNumber++
// 		écrire :
		"<tr class='tableBody' id='line" + lineNumber + "'>" +
			"<td class='hour'>" + heureCourante + "h</td>"
// 		pour chaque agenda(i) : regarder si rowspan(i) =! 1
		for i = 1 ; i == 3 ; i++
			if rowspan(i) =! 1 then
//				- oui : pas de nouvelle ligne et rowspan(i)--
				rowspan(i)--;
//				- non : regarder si il n'y a pas un d'événement
			else		if listeEvent(i).noEvent()
//						- oui : class='noEvent'
						classValue="noEvent";
//						- non : class='event'
//								récupérer son nom : nomEvent
//								regarder sa durée : x heure(s) -> rowspan(i)='x'
						classValue="event";
						nomEvent=getNomEvent();
						rowspan(i)=getDuree();
//					écrire :
					"<td class='column" + i + " " + classValue + "' rowspan='" + rowspan(i) + "'>" + nomEvent +"</td>" +
		end;
//			écrire :
			"</tr>"
		heureCourante++;
	end;
	
// écrire la fin du code :
		"</table>" +
	"</body>" +
"</html>"

// FIN
