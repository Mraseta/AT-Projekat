Agenti sakupljači: agenti koji sakupljaju podatke za izračunavanje predikcije ishoda utakmice na
osnovu postojećih mečeva. Podaci se nalaze na više računara. Svakom agentu treba da se
prosledi utakmica (timovi) čiji će se ishod izračunati.

Agent predikcija: Agenti sakupljači šalju podatke o prethodnim utakimicama između ovih timova
agentu koji računa predikciju ishoda meča.

Master agent: Konačan rezultat se šalje master agentu koji korisniku prikazuje konačan ishod.

Svaki server ima svoju datoteku utakmice.txt u kojoj su podaci o utakmicama dati u obliku: TIM_A-TIM_B-GOLOVI_A-GOLOVI_B. 
Agent sakupljač nakon što dobije dve ekipe čiji ishod utakmice treba da predvidi, sakuplja sve utakmice između te dve ekipe sa svog
i ostalih servera. Zatim ih prosleđuje prediktoru koji predviđa ishod, a on to šalje master agentu koji obaveštava korisnika o predikciji.
