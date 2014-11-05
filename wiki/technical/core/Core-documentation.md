<!-- --- title: Technická dokumentace část druhá: Dokumentace jádra systému -->

[[_TOC_]]

## Úvod

Pikater je multiagentní systém vyvíjený týmem Mgr. Romana Nerudy, CSc. na Matematicko-fyzikální fakultě Univerzity Karlovy a je úzce spojen s Ústavem informatiky Akademie věd České Republiky. Systém byl vyvíjen za účelem vytvořit výpočetní platformu pro akademický výzkum v oblasti strojového učení. Prošel mnoha podobami. Využíval se jako výpočetní model pro výzkum v oblasti umělé inteligence na Edinburgské univerzitě, rovněž jako výukový program poskytující úvod do problematiky umělé inteligence pro studenty středních škol. Zároveň slouží jako univerzální prostředí pro ověření správnosti nových nápadů členům katedry.

### Motivace

Procesy strojového učení patří mezi výpočetně velmi náročné úlohy. Je mnoho způsobů, jak učit model rozhodovat obecné problémy a není snadné predikovat, který způsob strojového učení je pro daný typ problému optimální. Problém volby optimální metody strojového učení je v systému řešen analýzou vstupních dat. Na základě podobnosti vstupních datových množin je z již vypočtených výsledků v systému vybírána metoda strojového učení, která byla v minulých pokusech nejúspěšnější. Metody strojového učení poskytují mnoho parametrů, jimiž lze přímo ovlivnit proces učení, testování i validace. Pikater se snaží vhodnými způsoby procházení stavového prostoru parametrů:
* ušetřit strojový čas učení,
* zvolit vhodný poměr explorace a exploatace pro zajištění konvergence procesů učení co možná nejblíže k lokálnímu optimu.

### Technologie

**JADE**  
Systém je postaven nad multiagentní middleware platformou JADE vyvíjenou italským Telecomem. Jedná se o vhodnou platformu pro modulární členění jednotlivých metod strojového učení. Hlavní přednosti jsou:
* Již vyřešená infrastruktura a snadná integrace nových agentů (metod strojového učení, komponent a tak dále).
* Jednoduché uložení/načtení naučených modelů - uspávání/probouzení agentů.
* Komunikační mechanismy, které transportují zprávy mezi agenty.

**JADE gateway**  
Brána do systému. Umožňuje poslat agentovi zprávu z prostředí mimo JADE.

**WEKA**  
Sada předprogramovaných algoritmů a datových struktur pro strojové učení. Ve většině případů využívají výpočetní agenti Pikateru nějakým způsobem rozšířené metody z knihovny WEKA.

**JPA**  
JPA framework je použit pro ukládání JAVA objektů pomocí objektově relačního mapování, nezávisle na užitém databázovém systému. Jedinou výjimku z tohoto pravidla tvoří ukládání velkých souborů - projekt využívá databázi PostgreSQL, která soubory ukládá do tzv. "LargeObjektů".

**XStream**  
Knihovna pro serializaci/deserializaci JAVA objektů z/do XML.

### Terminologie

**Jádro**  
Jádro je "výpočetní jednotka" projektu a zároveň celý původní projekt Pikater. Spolupracuje s webovým GUI a udržuje jisté standardy, aby ho bylo možno propojit s jinými platformami multiagentních, případně expertních systémů. Speciálním typem použití je pak nasazení na superpočítač a ke kompilaci nejsou proto potřeba žádné externí knihovny.
Tento dokument se zabývá právě dokumentací jádra.

**Dataset**  
Obecný termín pro datovou množinu.

**Experiment**  
Logická struktura zahrnující vstupní datasety, popis jak se mají využít a co se má vypočítat. Dělí se na "úlohy".

**Úloha**  
Dále nedělitelná podčást experimentu, nezávislá na dalších úlohách.

**Dávka**  
Experiment přetransformovaný do seznamu úloh.

**Prohledávače**    
Agenti, jejichž účel je generovat a hledat optimální parametry dané metody strojového učení v rámci aktuálního 
experimentu.

**Doporučovače**  
Agenti, jejichž účel je vybírat co nejlepší metodu strojového učení v závislosti na vstupních datech aktuálního problému. Jako metrika vhodnosti je vybrána podobnost vstupních dat aktuálního problému a předchozích problémů.

**Ontologie**    
Obecný termín pro zprávu (JAVA objekt) posílaný mezi agenty prostřednictvím komunikačních mechanismů platformy JADE.

**Krabička**  
Označení pro autonomní část experimentu (z logického úhlu pohledu). Experimenty jsou ve webovém rozhraní definovány pomocí "spojování krabiček".

## Původní jádro

Funkce původního systému jsou:
* Efektivní procházení stavového prostoru parametrů jednotlivých algoritmů.
* Využití doporučovačů.
* Zpracování experimentů představující jednu úlohu (trénování a testování, případně ukládání modelu). Úlohy mohly být spouštěny postupně na několika vstupních datasetech a s různými parametry buďto zadanými uživatelem nebo vygenerovanými nějakým prohledávačům.
* Bylo možné si vybrat konkrétní metodu strojového učení, evaluační metodu a nastavit parametry ovlivňující řízení běhu výpočtu. Nebyla-li metoda strojového učení uživatelem zadána, musel být vybrán nějaký doporučovač.
 
## Nové jádro

V této kapitole pojednáme o změnách jádra oproti původní verzi.

### Kompletní přestavba

Z podoby připomínající skript, který se spouštěl z příkazové řádky má dnes Pikater podobu interaktivní aplikace podporující jak konzolový, tak i grafický režim (přes webové stránky).

Z hlediska struktury kódu byl pro jádro vytvořen balíček `org.pikater.core`, ve kterém se jednotliví agenti dělí na základě jejich funkce na "systémové" a "experimentové". Systémoví agenti se starají o běh systému a zajišťují infrastrukturu experimentovým agentům.

Došlo k přeuspořádání agentů, jehož vedlejším efektem bylo téměř perfektně bijektivní zobrazení experimentových agentů na zadefinované krabičky. Hlavní změny:
* Spojení agentů `Manager` a `OptionManager`.
* Rozdělení jediného výpočetního agenta obsahujícího všechny algoritmy strojového učení na několik samostatných výpočetních agentů (za použití vhodného polymorfismu).
 
Kompletní přestavbou prošla také většina ontologií. Byly hierarchizovány do jmenných prostorů (se snahou udržet mapování na agenty), a většina i zobecněna nebo rozšířena. Samozřejmě také přibylo mnoho nových, aby systém splnil specifikaci projektu.

Dále došlo k výměně ontologie `Option`. Původní verze využívala k reprezentaci informací výlučně textové řetězce. Nová verze je obecnější (typovaná), rozšiřitelná, validovatelná a lze do ní ukládat omezení na hodnoty pro každý typ.

Všechna sdílená data jsou nyní ukládána do objektově-relační databáze, kterou využívá i webové rozhraní.

**Schéma nové architektury**

[[/docs/technical/architektura-final.png]]

### Dávky

Motivací pro tuto funkci byla podpora paralelního spouštění úloh, zrychlení a zrobustnění výpočtů. Pro reprezentaci dávky byla v jádře systému vytvořena ontologie `ComputationDescription`.

### Univerzální formát

Nová struktura sloužící jako obecný prostředník mezi interními formáty experimentů jádra (dokonce i konzolových GUI agentů) a webové nadstavby. Univerzální formát tedy:
* představuje formát, v němž jsou experimenty ukládány do databáze,
* definuje experimenty (nepřímo) jako grafické krabičky spojené hranami a lze do něj uložit speciální informace webové reprezentace (aktuálně jsou vyžadovány pouze pozice krabiček).
* je navržen, aby byl rozšiřitelný oběma směry (pro jádro i webové rozhraní) a snaží se o vzájemné odstínění.

Aktuální návrh vychází z myšlenky obalit vnitřní ontologie dávky, přičemž až na jisté výjimky se každý takový obal nepovinně odkazuje na třídu s informacemi pro GUI.

V kódu ho lze nalézt v balíčku `org.pikater.shared.experiment`. Třída reprezentující celou strukturu se nazývá `UniversalComputationDescription`.

### Zrychlení

Původní jádro mělo dva stěžejní výkonnostní problémy:

1. **Sekvenční počítání**  
Vyřešeno pomocí distribuovaného počítání a efektivního plánování, které se snaží zbytečně nevytěžovat síťovou infrastrukturu přesouváním velkých dat.
2. **Přenos dat prostřednictvím ACL zpráv**  
Řešení je vysvětleno dále v dokumentu.

#### Přenos dat

Jedním z požadavků na nový systém bylo zefektivnění datových přenosů mezi agenty. Tato kapitola popisuje způsob reprezentace a distribuce dat a kroky provedené pro optimalizaci přenosů.

**Reprezentace dat**  
Vnitřní reprezentace uložených datasetů má podobu formátu ARFF - nativního formátu knihovny WEKA. ARFF soubor je v podstatě textový CSV soubor obohacený o metadata v podobě anotací. Podrobnou dokumentaci lze nalézt zde:  
<http://weka.wikispaces.com/ARFF>

Využití tohoto formátu bylo upřednostněno před zavedením vlastního nového formátu z několika důvodů:
* Abychom nemuseli měnit existující "parsery".
* Pro jeho kvalitní dokumentaci a přívětivost jak pro lidské, tak i strojové zpracování.

Na druhou stranu, formát se nezabývá kompresí a soubory tedy mají tendenci být poměrně velké. Problém sice lze řešit dodatečnou kompresí, ale na úkor výkonu.

V paměti (a systému) jsou datasety reprezentovány ontologií `DataInstances` (datovými instancemi). Je to obálka pro data převoditelná z a do formátu zpracovatelného knihovnou WEKA (`weka.core.Instances`).

**Řešení přenosu**  

V původním Pikateru se načtená data předávala výpočetním agentům přímo ACL zprávami, což se ukázalo jako nanejvýš neefektivní – potenciálně velká data musela projít drahou serializací a deserializací, dokonce i v rámci jednoho JADE containeru.

Nyní jsou data přenášena z databáze přímo do agenta dedikovaného pro přístup k databázi, který se vyskytuje v každém containeru. Aktuální systém předpokládá výskyt právě jednoho slave containeru na jednom fyzickém stroji.

V rámci daného containeru (stroje) jsou pak data přenášena tzv. "object to agent" rozhraním, které platforma JADE poskytuje. K přenosu dat vlastně dojde pouze předáním odkazu do stejného adresního prostoru, bez zbytečné serializace nebo kopírování dat.

Pro zefektivnění přenosů mezi různými stroji pak bylo zavedeno cacheování dat. Data se na každém stroji zachovávají (nemažou) a pokud již jsou na daném stroji k dispozici, nepřenášejí se systémem znovu.

Všimněte si ovšem, že některá data se stále přenášejí ACL zprávami, protože jde typicky o malé soubory (do 5 MB) a jejich přenos nepředstavuje riziko velkého vytížení infrastruktury. Konkrétně jsou to:
* __Serializované modely__  
Typicky mívají do 100 kB, jsou doručovány binárně (bytová pole) v podobě serializované JAVA třídy (agenta). Serializaci/deserializaci zajišťuje WEKA.
* __JAR soubory__  
Externí agenti a metody strojového učení (definované uživateli).

I tato data se cachují.

### Interakce s webovým GUI

Jádro webovému rozhraní poskytuje:
* Seznam aktuálně zadefinovaných krabiček pro grafický editor experimentů, s veškerými doprovodnými informacemi (parametry metod strojového učení a tak dále). Pro tento účel byla vytvořena ontologie `AgentInfo` reprezentující jednu krabičku.
* API přejímající informace o událostech, např. "byl vydán příkaz ke spuštění tohoto experimentu".

Od webového rozhraní nevyžaduje jádro naopak nic - musí i nadále pracovat jako nezávislá jednotka.

## Popis agentů

### Systémoví agenti

#### Agent_Initiator

Spouští jak master container, kde se spravují systém a výpočty, tak i vyžadované slave containery, které by měly převážně být vytěžovány výpočetními agenty. Způsob spouštění systému je ve všech případech stejný a liší se jen:
* iniciační parametr,
* jméno konfiguračního souboru.

V případě slave containeru se přidává ještě druhý parametr označující pořadové číslo.

Agent je naprogramován dostatečně obecně, aby ho použít i prostřednictvím Ant. 

##### Spuštění systému z konzole

1. Spustit JADE.
2. Spustit tohoto agenta (ve smyslu příkazu `java`). Spuštěni jsou agenti definovaní v konfiguračním souboru, jehož cestu agent dostane jako argument, nebo není-li žádný takový argument definován, implicitně se pokusí načíst `core/Configuration.xml`.

##### Spuštění systému přes skripty

Nejpohodlněji lze systém zapnout pomocí bash skriptů: jeden zajišťuje buildování jádra systému, další spouštění hlavního containeru a poslední spouštění slave containeru s daným číselným parametrem. Tyto skripty pouze volají Ant na konfiguračním souboru `buildCore.xml` v kořenovém adresáři projektu.

#### Agent_ManagerAgent

Tento agent se nachází na každém containeru právě jednou. Na master i slave containeru má funkci vládce.

Přijímá následující ontologie bez odpovědi:
* `CreateAgent`  
Signál pro vytvoření agenta specifikovaného ve zprávě.
* `KillAgent`  
Signál pro ukončení agenta specifikovaného ve zprávě.
* `SaveAgent`  
Signál pro uložení již natrénovaného modelu (agenta).
* `LoadAgent`  
Signál pro oživení již natrénovaného modelu (agenta).

Agenti se ukládají i oživují ze souborů umístěných na filesystému. Přepravu souborů z filesystému do databáze nebo na jiný stroj řeší modul pro přepravu dat.

Přijímá následující ontologie s odpovědí:
* `GetComputerInfo`  
Odpovídá ontologií `ComputerInfo`. Vrací informace o počítači, na kterém běží, například počet jader procesoru.
* `Ping`  
Odpovídá zasílateli, aby bylo vidět, že container "žije".

**Vytvoření agenta**  

{{{{{{
Manager->+ManagerAgent: CreateAgent (type)
activate Manager
ManagerAgent->ManagerAgent: starts new agent
ManagerAgent-->>-Manager: (agent AID)
}}}}}}

**Vytvoření agenta s natrénovaným modelem**  

{{{{{{
Manager->+ManagerAgent: LoadAgent (ID)
activate Manager
ManagerAgent->+DataManager: GetModel (ID)
DataManager-->>-ManagerAgent: (agent)
ManagerAgent->ManagerAgent: starts saved agent
ManagerAgent-->>-Manager: (agent AID)
}}}}}}

#### Manager

Nejdůležitější agent celého systému. Přejímá, transformuje, přeposílá a řídí zadané výpočty.

Přijímá následující ontologie bez odpovědi:
* `NewBatch`  
Signál pro spuštění nového experimentu. Obsahuje jednoznačný identifikátor experimentu i uživatele, který ho zadal.
* `LoadBatch`  
Signál pro načtení experimentu. Experiment se získá z agenta `DataManager` ve formě ontologie `Batch` obsahující všechny relevantní informace.

**Rámcový postup pro spuštění nového experimentu:**

1. Třídou `Parser` je experiment rekurzivně (v daném pořadí) transformován na:
	* orientovaný, acyklický multigraf,
	* podexperimenty,
	* úlohy.
2. Úlohy jsou již během transformace registrovány do struktury front (v tomto agentu), které zachovávají závislosti.
3. Všem úlohám jsou přiřazeny identifikátory, protože JADE nezachovává při sdílení reference JAVA objektů.
4. Struktura front je pak přeposlána plánovači (agent `Agent_Planner`).
5. Plánovač postupně úlohy spouští a výsledky jsou registrovány v tomto agentu.
6. Jakmile se dokončí výpočet poslední úlohy, výpočet experimentu končí a nastaví se mu status na "FINISHED".

#### Agent_Planner

Slouží k efektivnímu plánování příchozích úloh (prostřednictvím ontologie `Task`), které představují jeden problém pro jednoho výpočetního agenta nebo data processing.

Důvodem vzniku tohoto agenta je snaha maximálně odstínit problematiku spouštění úloh od parsování dávek a řízení výpočtů. Distribuované výpočty (paralelní spouštění úloh) přináší do systému poměrně velký zmatek:
* Úlohy jsou spouštěny na několika containerech umístěných na různých strojích.
* V každém containeru může zároveň běžet několik úloh.

Je třeba vyřešit vzniklý problém přepravy dat mezi agenty.
* Běží-li výpočty pouze na jednom stroji, můžeme využít sdíleného filesystému.
* Přeprava dat mezi containery probíhá prostřednictvím `DataManager` agenta - jeden existuje pro každý container. Velká data jsou přepravována přes síťové sockety a tedy mimo JADE.

Přijímá následující ontologie bez odpovědi:
* `ExecuteTask`  
Signál pro zadání jedné úlohy (pro jednoho agenta). Přijaté úlohy se řadí do datové struktury `WaitingTaskQueues` představující skupinu prioritních front. Po doručení každé nové úlohy dochází k přeplánování.
* `KillTasks`  
Signál pro "ukončení" úloh jistého experimentu - plánovač je vyřadí ze struktury front. Nedopočítané úlohy nejsou nijak rušeny a úspěšně doběhnou.

Přijímá následující ontologie s odpovědí:
* `GetSystemLoad`  
Odpovídá zasláním informací o aktuálním vytížení systému - aktuální využití všech CPU jader v rámci celého slave containeru. Informace jsou získány z třídy `CPUCoresStructure`, s jejíž pomocí funguje rozvrhování úloh. 

Rozvrhování se spouští po detekci změny, jež by mohla ovlivnit optimalitu rozvržení úloh. Typickými zástupci takových podnětů jsou příjem nové úlohy a dokončení/ukončení úlohy.

Proces plánování:

1. Zjistíme, zda je k dispozici alespoň jedna úloha - pokud ano, vybere se ta z nich, která má nejvyšší prioritu. Nikoliv pouze nejvyšší celkovou prioritu, nýbrž i prioritu zohledňující průchodnost systému pro (podle predikce) krátce trvající experimenty. Jako první se tedy vybírají úlohy s nejvyšší prioritou, které zároveň nebudou trvat dlouho.
2. Vyhledají se nové, v plánovači dosud nezaregistrované slave servery. Zároveň s tím musí proběhnout změna struktury `CPUCoreStructure`, kam přibude informace o nových volných CPU jádrech.
3. Všem registrovaným agentům `AgentManager` se pošle ontologie `Ping`, abychom ověřili dostupnost výpočetních agentů/containerů.
4. V případě, že nějaký container "umřel", musí být všechny jeho nedopočítané úlohy navráceny do struktury front.
5. Pro vybranou úlohu se ze třídy `DataRegistry` (pro každou vstupní datovou závislost) určí seznam strojů, na nichž se soubor nachází, a zjistí se tak, jsou-li k dispozici všechna data. Pokud ano, plánovač se pokusí úloze přiřadit volné procesorové jádro na stroji, kde jsou alespoň nějaká data potřebná pro její výpočet.
6. Nejsou-li vstupní data aktuálně dostupná na žádném stroji, plánovač se pokusí znovu spustit úlohy, které měly již dříve tato data spočítat, vybraná úloha je navrácena zpět do struktury front a znovu dojde k přeplánování.
7. Pokud jsou všechna data k dispozici a pro výpočet existuje volné jádro, požádá plánovač agenta `ManagerAgent` na stroji, kde se nachází dané CPU jádro, aby vytvořil nového výpočetního agenta potřebného typu a zaslal plánovači v odpovědi jeho jméno.
8. Plánovač vytvořenému agentovi pošle vybranou úlohu a označí vybrané výpočetní jádro za obsazené.
9. Po dokončení úlohy se plánovači vrátí informace o spočtených souborech. Plánovač si je uloží do třídy `DataRegistry` a odešle vypočtenou úlohu zpět agentovi `Manager`.

Jakmile agenti dokončení výpočet svých úloh a navrátí výsledky, jsou sami ukončeni.

#### AgentInfoManager

Slouží ke sbírání informací (instancí třídy `AgentInfo`) o experimentových agentech. Využívá je jak webové GUI, tak i doporučovači. Až na výjimky se dá říci, že jeden agent vždy produkuje jednu instanci `AgentInfo`, která odpovídá jedné grafické krabičce.

Přijímá následující ontologie s odpovědí:
* `GetAgentInfo`  
Obsahuje ID uživatele, pro něhož se má sestavit seznam přípustných krabiček (instancí třídy `AgentInfo`). Tento seznam je navrácen a obsahuje jak defaultní experimentové agenty, tak i externí agenty nahrané do aplikace samotným uživatelem.

Přijímá následující ontologie bez odpovědi:
* `NewAgent`  
Signál pro zaregistrování nového externího agenta do systému.  
Agent je vytvořen (stačí načíst jeho ".JAR" soubor a vyzvednout si jeho plné jméno z příchozí zprávy), požádán o vypublikování informací o sobě (ontologie `AgentInfo`) a publikované informace jsou uloženy do databáze prostřednictvím agenta `DataManager` a ontologie `SaveAgentInfo`. Ihned poté je agent ukončen.

Agent při zapnutí vždy ověřuje, že jsou v databázi uloženy informace o všech experimentových agentech. Pakliže nejsou, všichni takoví agenti jsou ihned vytyčeným postupem zaregistrováni.

#### Agent_KlaraGUIAgent

Agent je pojmenovaný po Mgr. Kláře Peškové, autorce původního Pikateru. Vznikl jako další "vstupní bod" do jádra, nezávislý na webové nadstavbě.

Po zapnutí agenta se spustí jednoduchý komunikační automat, který umožňuje v konzolovém režimu zadat nový experiment, přidat nový dataset a vygenerovat pro něj metadata. Nový experiment se načítá v podobě instance třídy `ComputationDescription`, serializované do XML souboru na filesystému. Konverze formátu z/do JAVA objektů zajišťuje knihovna XStream.

Komunikace probíhá přes agenta `GUIComunicator`, jehož snahou je vytvořit jednotné přístupové rozhraní pro všechny GUI agenty jako je tento. V praxi si tedy může kdokoli vytvořit vlastní přístupový bod do systému, s vlastní definicí dávek. Nakonec je ovšem samozřejmě nutné implementovat konverzi do `ComputationDescription`, kterou používá jádro.

#### Agent_Mailing

Nový agent. Zprostředkovává zbytku systému službu posílání emailů. Vyžaduje, aby na daném stroji běžel lokální SMTP server.

Přijímá následující ontologie bez odpovědi:
* `SendEmail`  
Signál pro zaslání emailu specifikovaného zprávou na jednu emailovou adresu. 

Aktuálně je agent je v systému využíván pouze při dokončení experimentu.

**Odesílání notifikace o dokončeném výpočtu**  

{{{{{{
Agent\nManager->+Agent\nDataManager: updateExperimentStatus
opt new experiment status = FINISHED
activate Agent\nManager
Agent\nDataManager->+Agent\nMailing: SendEmail
  Agent\nMailing->Agent\nMailing: sends e-mail notification\nvia local SMTP server
  Agent\nMailing-->>-Agent\nDataManager: 
end
Agent\nDataManager-->>-Agent\nManager:
}}}}}}

#### Agent_ARFFReader

Agent je využíván k načítání ARFF souborů z filesystému a jejich transformaci na ontologie.

Přijímá ontologii `GetData` obsahující jméno souboru a jako odpověď zasílá ontologii `DataInstances`.

**Načítání dat**  

{{{{{{
ComputingAgent->+ARFFReader: GetData
activate ComputingAgent
opt data not cached
  ARFFReader->+DataManager:
  DataManager->DataManager: gets data from DB,\nstores them locally
  DataManager-->>-ARFFReader:
end
ARFFReader->ARFFReader: loads data
ARFFReader-->>-ComputingAgent: (data - O2A)
}}}}}}

### Experimentoví agenti

Tvoří základní stavební kameny pro experimenty. Nezabývají se technickými problémy ani zajištěním běhu Pikateru, nýbrž pouze a výlučně implementací jednotlivých metod strojového učení. Nacházejí se v samostatné package `org.pikater.core.agents.experiment`, dědí od agenta `AbstractExperiment` a dělí se podle kategorií na:
* výpočetní agenty,
* doporučovače,
* prohledávače,
* data processingy.

Tito agenti nejsou jednoznačně přiřazeni krabičkám. Abychom tohoto stavu docílili, vytvořili jsme speciální podbalíček `virtual` a v něm tzv. "virtuální agenty", jejichž jediným účelem je deklarovat a vracet instance třídy `AgentInfo`.

Seznam krabiček si za běhu systému nepřímo žádá jak webové rozhraní (viz výše), tak i samotné jádro. Jsou potřeba jak pro zadefinování experimentu z webového rozhraní do "univerzálního formátu" (viz výše), tak i pro rozparsování tohoto formátu do struktury více vyhovující jádru, až bude zadán příkaz ke spuštění experimentu.

#### Data processing agenti

Představují "datové transformace", dědí od agenta `DataProcessing`. Na vstupu vyžadují data a na výstupu vydávají nějakým způsobem transformovaná data. Motivací pro jejich vznik byla potřeba preprocessingu vstupních dat a postprocessingu dat vzniklých strojovým učení.

Data mají v celém systému formát ARFF a tento agent tedy využívá pro čtení i zapisování dat agenta `ARFFReader`.

V experimentech jsou data processing agenti identifikování ontologií `DataProcessing`.

Existence data processingu umožňuje učit modely postupně, případně vytvářet dva modely, kde jeden řeší lépe problém pro data jednoho typu a druhý pro data jiného typu. Jako příklad uvedeme agenta `WeatherSplitter`, který obdrží na vstupu dva vstupní datasety se stejnou strukturou a na základě hodnoty v jistém sloupečku je rozdělí na tři soubory.

#### Výpočetní agenti

Specifičtější forma data processingu. Nejedná se o pouhé datové transformace, nýbrž přímo o proces strojového učení. Většinou je k tomu využita knihovna Weka, ale nic nebrání vlastní implementaci jakékoli metody. Tyto agenty nazýváme výpočetní, protože provádějí časově nejnáročnější operace v systému, a proto se výpočty distribuují na více strojů. Pro reprezentaci výpočetního agenta se v experimentech používá ontologie `ComputingAgent`.

Přijímají následující ontologie bez odpovědi:
* `Task`  
Signál pro provedení procesu trénování, testování a validace. Vše určuje úloha obsažená ve zprávě.

**Natrénované modely**  
Dle specifikace serializují výpočetní agenti (v závislosti na úloze) svůj model a posílají ho jako součást výsledku výpočtu v ontologii `Task`. Agent `Manager` tento model přepošle k uložení agentovi `DataManager`, který ho uloží do databáze s vazbou na daný výsledek a experiment.

Serializaci modelu zajišťuje metoda zděděná od předka:  
`org.pikater.core.agents.experiment.computing.Agent_ComputingAgent.getAgentObject()`.

Dříve uložený model lze oživit prostřednictvím metody:  
`org.pikater.core.ontology.subtrees.management.LoadAgent()`.

**Uložení natrénovaného modelu**  

{{{{{{
ComputingAgent-->>+Manager: (result for Task)
destroy ComputingAgent
Manager->+DataManager: SaveResults (Task)
DataManager->DataManager: saves result to DB
opt model included in Task result
DataManager->DataManager: saves model for the result
end
DataManager-->>-Manager: 
}}}}}}

**Vytvoření agenta s natrénovaným modelem**  

{{{{{{
Manager->+ManagerAgent: LoadAgent (ID)
activate Manager
ManagerAgent->+DataManager: GetModel (ID)
DataManager-->>-ManagerAgent: (agent)
ManagerAgent->ManagerAgent: starts saved agent
ManagerAgent-->>-Manager: (agent AID)
}}}}}}

**Načítání dat pro úlohy**  

{{{{{{
ComputingAgent->+ARFFReader: GetData
activate ComputingAgent
opt data not cached
  ARFFReader->+DataManager:
  DataManager->DataManager: gets data from DB,\nstores them locally
  DataManager-->>-ARFFReader:
end
ARFFReader->ARFFReader: loads data
ARFFReader-->>-ComputingAgent: (data - O2A)
}}}}}}

#### Prohledávače

Využívají se pro procházení množiny parametrů, dědí od abstraktního agenta `Searcher` a v experimentech jsou reprezentováni ontologií `Searcher`.

K efektivnímu procházení stavového prostoru se používá mnoho přístupů - od nejjednodušších využívajících náhodný výběr hodnoty parametru (např. `RandomSearch`), přes složitější (`ChooseXValues`), po složité prohledávače využívající genetických a evolučních algoritmů. Složitější agenti obsahují více nastavení a nabízí širší možnosti prohledávání.

Přijímají následující ontologie s odpovědí:
* `GetParameters`  
Odpovědí je ontologie `ExecuteParameters` obsahující vygenerované/vypočtené parametry podle omezení na rozsah nebo množinu povolených hodnot získaných z přijaté zprávy.

Přijímají následující ontologie bez odpovědi:
* `Eval`  
Zpětná vazba úspěšnosti vygenerovaných parametrů. Jako metrika jsou použity míry chyb naučeného a otestovaného modelu.

#### Doporučovače

Jejich úkolem je vybírat pro daná vstupní data vhodnou metodu strojového učení. V praxi dostanou hash vstupního souboru trénovací množiny dat a vydávají plné jméno třídy agenta, který by měl pro daná vstupní data vytvořit nejkvalitnější/nejúspěšnější model. Dokáží poradit nejen metodu strojového učení, ale i nastavení jejích parametrů - k tomu využívají primárně podobnost vstupních datových množin. Pro porovnávání dat se nepoužívají přímo velké vstupní soubory, nýbrž vygenerovaná metadata (v zájmu efektivity a snadnějšího zpracování). Metadata byla navržena tak, aby dokázala pojmout informace o každém sloupci datového ARFF souboru a aby obsahovala k jednotlivým sloupcům i jisté typované statistiky.

Všimněte si, že vstupní data nemusí nutně být vstupní data celého experimentu - mohou to například být i data spočtená předchozí dílčí úlohou. Jedinou podmínkou je, aby měla vygenerovaná metadata.

**Proces doporučení:**  
Jakmile je nalezena povahově nejbližší datová množinu (ke vstupním datům), projdou se prostřednictvím agenta `DataManager` výsledky všech předchozích výpočtů s ní souvisejících a vybere se ten "nejúspěšnější". Ke každému výpočtu (úloze) lze dohledat agenta, který tohoto výsledku dosáhl.

#### Agent_DataManager

Původně byl Pikater vytvořen jako multiagentní a maximálně adaptibilní systém, jehož architektura si kladla za cíl co nejvíce odstínit funkcionalitu od použité technologie a správy systému. V našem projektu bylo dalším úkolem odstínit systém od specifik původního databázového systému a proto vznikl speciální agent zajišťující uchovávání dat. Jako vedlejší produkt je tak odstíněna i samotná existence databáze. Agent komunikuje opět pouze pomocí ontologií a má jako jediný v jádře systému možnost přímo číst a měnit stav databáze.

Webové GUI využívá na rozdíl od jednotlivých agentů přímý přístup do databáze. Teoreticky by šlo i z webového GUI využívat pro manipulaci s databází tohoto agenta, ale z mnoha důvodů se jedná o naprosto nevhodnou variantu:
* Velice podstatně by se zvýšil počet definovaných ontologií a komplexnost systému.
* Došlo by k razantnímu zpomalení aplikace.
* Webové GUI by se stalo zcela závislé na jádru.

**Použité ontologie**  
Můžeme je rozdělit do několika logických oblastí:
* Pro získání základních informací o uživateli a překlad přihlašovacího jména na ID se používají ontologie `GetUser` a `GetUserID`.
* Pro překlad jména souboru na hash se využívá ontologie `TranslateFilename`.
* Pro ukládání a načítání struktury `AgentInfo` se používají ontologie `SaveAgentInfo` a `GetAgentInfo`. Existují i ontologie pro získání všech `AgentInfo` interních i externích krabiček uložených v databázi.
* Pro práci s experimenty se používají ontologie `SaveBatch`, `LoadBatch`, `UpdateBatchStatus`, `GetBatchPriority`.
* Podobně koncipované jsou ontologie pracující s experimenty: `SaveExperiment` a `UpdateExperimentStatus`.
* Analogicky se ukládají i výsledky: `SaveResults`, `LoadResult`. Ontologie `Result` ovšem slouží pouze pro reprezentaci chyb natrénovaného modelu a neobsahuje tudíž žádné datové množiny.
* Pro ukládání datových množin slouží ontologie `SaveDataset`. Načítání provádí ontologie `GetFile`. Speciálním případem jsou pak externí agenti - pro ně je k dispozici ontologie `GetExternalAgentJar`. Ukládání agentů provádí pouze webové GUI, tudíž ontologie "SaveExternalAgent" neexistuje.
* Pro ukládání a čtení vygenerovaných metadat se využívají ontologie `SaveMetadata`, `GetMetadata`, `GetAllMetadata` a `GetMultipleBestAgents`.

#### Agent_MetadataQueen

Tento agent generuje metadata pro jednotlivé datové množiny, jak vstupní tak i výstupní. Počítá statistické veličiny, které se ve výsledku používají k odhadu podobnosti dvou datasetů bez časově náročné manipulace a opětovného načítání.

Přijímá následující ontologie bez odpovědi:
* `NewDataset`  
Signál pro vygenerování metadat k novému uživatelem zadanému datasetu.
* `NewComputedData`  
Signál pro vygenerování metadat k nové datové množině vypočtené systémem. Výsledná metadata jsou prostřednictvím ontologie `Metadata` odeslána agentu `DataManager`, který je uloží a sváže s daným datasetem.

### Externí agenti

Specifikace ukládá umožnit přidávání nových "komponent" – agentů, kteří musí být specializací jednoho z typů definovaných systémem (výpočetní agent, doporučovač, prohledávač, ...). Uživatel musí dodat implementaci v podobě JADE agenta, který na požádání pošle svoji konfiguraci (mj. pro účely zobrazení ve webovém GUI).

Uživatel má možnost skrz webové rozhraní nahrát do systému JAR soubor obsahující zkompilovaný kód implementující funkčního agenta, dědícího od třídy `org.pikater.core.agents.PikaterAgent` nebo některé specializace, např. `org.pikater.core.agents.experiment.dataprocessing.Agent_DataProcessing`.

Agenta lze ovšem ve webovém GUI použít až po jeho schválení některým z administrátorů aplikace. Jakmile k tomu dojde, jeho použití je stejné jako všech ostatních experimentových agentů, s jedinou výjimkou: JAR soubory musí jádro před během experimentu získat a začlenit do `classpath`. To zajišťuje automaticky platforma JADE, která při startu systému vyžaduje zadání cesty k JAR souborům na lokální disku jako parametr příkazové řádky. Příklad:

`-jade_core_management_AgentManagementService_agentspath <cesta>`

V našem případě je to:

`-jade_core_management_AgentManagementService_agentspath core/ext_agents`

Ukázkovou implementaci externího agenta lze nahlédnout ve třídě `org.pikater.external.ExternalWekaAgent`. Chová se jako výpočetní agent `RBFNetwork`.

**Vytvoření agenta**  

{{{{{{
Manager->+ManagerAgent: CreateAgent (type)
activate Manager
opt external agent JAR not present locally
ManagerAgent->+DataManager: GetExternalAgentJar (type)
DataManager->DataManager: loads JAR\nstores it locally
DataManager-->>-ManagerAgent: 
end
ManagerAgent->ManagerAgent: starts new agent
ManagerAgent-->>-Manager: (agent AID)
}}}}}}

### Gateway agenti

Jádro si klade za cíl definovat komunikační rozhraní a poskytovat ho jako službu jiným komponentám. Slouží k tomu právě tito agenti.

Rozhraní je primárně koncipováno jako "předávání událostí a souvisejících informací". Původní snahou bylo poskytovat v jádře úplnou implementaci událostí pomocí RMI či RPC. Taková snaha měla ovšem velice nepříjemné dopady na vývoj a běhové vlastnosti systému a tak musí ostatní komponenty nejdříve připravit podmínky pro spuštění události (uložení dat do databáze) a teprve pak lze zavolat oznámení o události.

Veškeré rozhraní je definováno ve třídě `org.pikater.core.agents.gateway.WebToCoreEntryPoint` a předává události dále do systému prostřednictvím Gateway agentů. Aktuální implementace:

```java:/src/org/pikater/core/agents/gateway/WebToCoreEntryPoint.java```

## Popis ontologií

Všechny ontologie lze nalézt v balíčku `org.pikater.core.ontology`.

### AccountOntology

Tuto množinu ontologií využívají agenti, kteří vyžadují pro svůj chod informace z účtů uživatelů (ID, email a tak dále). K dispozici je dává agent `DataManager`.
    
### AgentInfoOntology

Jedná se o množinu ontologií používanou především pro vypublikování aktuálně podporované infrastruktury experimentů mimo systém.

Více informací lze nalézt v kapitole o agentu `AgentInfoManager` a doporučovačích.

### AgentManagementOntology

Přímo se týká vytváření, ukončování, zastavování a obnovování agentů.  
Adresátem těchto zpráv bývá agent `ManagerAgent`, který má v rámci svého containeru absolutní moc nad všemi ostatními agenty a jejich životním cyklu. Jeho služeb využívá agent `Initiator`, `AgentManager` a především `Planner`.

### BatchOntology

Zprostředkovává vytváření, načítání, přepravu a logování experimentů.

**ComputationDescription**  
Podontologie navržená k reprezentaci libovolného množství vzájemně datově závislých úloh (instancí třídy `DataProcessing`). Jedná se tedy o strukturu logicky představující multigraf, kde vrcholy jsou "výpočty" a hrany "toky dat" ("chyb" nebo datasetů).

Obsahuje:
* Seznam globálních přepínačů (optionů), které slouží ke specifické konfiguraci experimentů na globální úrovni.
* Seznam instancí třídy `FileDataSaver`.

Využívá se hlavně jako prostředek komunikace mezi výpočetními agenty a plánovačem nebo jinými agenty (prohledávači a doporučovači). Výpočetní agenti:
* vytvářejí natrénovaný model z trénovací, testovací a validační datové množiny,
* poskytují výsledky trénování a testování ve formě datové množiny a míry chyb, které se model dopouští na daných datech.

Ontologie je ve skutečnosti hack, který umožňuje nespecifikovat konkrétního výpočetního agenta a namísto něj:
* vyplnit nedefinovaného agenta,
* a zapojit k němu doporučovač.

Jestliže nemá výpočetní agent specifikovány všechny optiony, musí být napojen na prohledávač, který prochází množinu přípustných hodnot a pokouší se přiblížit k těm optimálním.

Dalším speciálním výpočetním elementem je evaluační metoda, která se zapojuje jako vstup pro výpočetního agenta. Evaluační metoda slouží k určení správné doby procesů učení modelu, aby se našel vhodný kompromis mezi nedostatečně vytrénovaným a přeučeným modelem.
    
### DataOntology

Tato množina ontologií má dva účely:
* jednak řídí přesuny velkých dat (říká, co se má kam poslat),
* a jednak se jejím prostřednictvím zasílají malá data (například ".JAR" soubory agentů).

Je využita například v těchto scénářích:
* Agent `ManagerAgent` žádá o poskytnutí ".JAR" souborů externích agentů v případě, že obdrží požadavek na vytvoření agenta, jehož zdrojové kódy zatím nemá na svém containeru k dispozici.
* Agent `ARFFReader` žádá o přepravu datasetu v případě, že data nejsou k dispozici na lokálním filesystému.
* Agent `Planner` využívá tuto ontologii pro řízení a ukládání vypočtených dat do databáze.

### DurationOntology 

Zaznamenává dobu výpočtu úloh, kterou měří `DurationAgent`.

### ExperimentOntology

Ontologie reprezentující dávky. Nejvíce ji využívá agent `Manager`, který za pomoci agenta `DataManagera` řídí výpočet experimentů, rozděluje je na podproblémy a loguje jejich změny.

### FileNameTranslationOntology

Prostřednictvím této ontologie dochází k překladu externího jména datasetu (jméno souboru) na systémem zpracovatelné interní jméno (zpravidla hash jeho obsahu). Tato konvence zjednodušuje a zefektivňuje proces řízení přepravy dat a zabraňuje zbytečné duplicitě dat na filesystému.

Překlad provádí agent `DataManager`, který spravuje párování externích a interních jmen souborů a dochází k němu:
* téměř ihned po zadání dávky ke spuštění - překládají se vstupní externí datasety,
* v agentu `Duration` pro překlad interního datasetu, na kterém se opakovaně spouští úloha lineární regrese představující pro systém jednu jednotku systémového času.

### MailingOntology

Tyto ontologie se využívají pro zadání povelu k zaslání určitého emailu. Aktuálně je pouze využívá agent `Manager` po dokončení výpočtu experimentů - zasílá informaci o dokončení výpočtu uživateli, který experiment zadal a přidá nejmenší chybu, které bylo při strojovém učení v dané dávce dosáhnuto.

### MetadataOntology

Využívají se:
* při generování a ukládání metadat pro datasety v agentech `Planner`, `MetadataQueen` a `DataManager`,
* pro porovnávání datasetů v doporučovačích (více informací v kapitole o doporučovačích).

### ModelOntology

Prostřednictvím této množiny ontologií dochází k ukládání/načítání modelů (agentů).

### RecommendOntology

Slouží ke zpřístupnění funkce doporučovačů zbytku systému, tj. vybírat pro dané datasety "nejlepší" metodu strojového učení (agenta) a "nejvhodnější" hodnoty nastavení.

### ResultOntology

Cílem této ontologie je poskytnout prostředek přepravy výsledků experimentů. Výsledky produkují pro jednu sekvenci trénování, testování a validace výpočetní agenti a z pohledu výzkumu strojového učení se jedná o nejdůležitější informace. Nejsou to výsledná data, nýbrž seznam chyb popisující úspěšnost naučeného modelu.

### SearchOntology

Slouží ke zpřístupnění funkce prohledávačů zbytku systému, tj. efektivně prohledávat stavový prostor modelů s důrazem na zachování vhodného poměru explorace a exploatace.

### TaskOntology

Množina ontologií umožňujících jednotlivým agentům pracovat s úlohami.  
Úlohy jsou řízeny agentem `Manager`, který je přeposílá agentu `Planner` a ten je distribuuje výpočetním agentům, případně data processingům. Jak data processingy, tak i výpočetní agenti přijímají výpočty pouze v podobě úloh.

#### Task

Obecná struktura/ontologie schopná pojmout jednu úlohu pro jakéhokoli agenta. Vlastnosti:
* Nese informace, se kterými lze identifikovat dávku, ke které úloha patří.
* Může obsahovat neomezené množství vstupních souborů potřebných pro výpočet.
* Obsahuje nastavení ovlivňující běh výpočtu.
* Obsahuje informace o předpokládané délce běhu výpočtu.
* Je obousměrná a po dokončení výpočtu úlohy je navrácena agentu `Manager`, obohacená o odkazy na vypočtené datasety a časy začátku a konce výpočtu.

### TerminationOntology

Ontologie pro ukončování agentů. Je zapotřebí k šetrnému "zabíjení" agentů - agenty nelze jednoduše ukončit násilím, protože služba JADE Directory Facilitator by pak byla ponechána v nekonzistentním stavu. Agenti jsou tedy "žádáni" o ukončení prostřednictvím této ontologie.
