package com.chichuka.birdvgvardc.navigation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.text.SimpleDateFormat
import java.util.*

// ==================== MODELS ====================
data class BirdGroup(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val birdType: String = "",
    val breed: String = "",
    val count: Int = 0,
    val age: String = "",
    val housingType: String = "",
    val notes: String = "",
    val lastVaccination: String = ""
)

data class VaccinationRecord(
    val id: String = UUID.randomUUID().toString(),
    val groupId: String = "",
    val vaccineName: String = "",
    val vaccineType: String = "",
    val batchNumber: String = "",
    val date: String = "",
    val nextDate: String = "",
    val administeredBy: String = "",
    val dosage: String = "",
    val route: String = "",
    val notes: String = "",
    val isCompleted: Boolean = false,
    val reactions: String = ""
)

data class MedicationCourse(
    val id: String = UUID.randomUUID().toString(),
    val groupId: String = "",
    val medicationName: String = "",
    val activeIngredient: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val frequency: String = "",
    val dosage: String = "",
    val withdrawalPeriod: String = "",
    val isActive: Boolean = true
)

data class SymptomRecord(
    val id: String = UUID.randomUUID().toString(),
    val groupId: String = "",
    val date: String = "",
    val symptoms: String = "",
    val severity: String = "Medium",
    val affectedCount: String = "",
    val diagnosis: String = "",
    val treatment: String = "",
    val notes: String = ""
)

// ==================== GLOBAL REPOSITORY ====================
object Repository {
    private const val PREFS = "bird_prefs"
    private lateinit var prefs: SharedPreferences

    val groups = mutableStateListOf<BirdGroup>()
    val vaccinations = mutableStateListOf<VaccinationRecord>()
    val medications = mutableStateListOf<MedicationCourse>()
    val symptoms = mutableStateListOf<SymptomRecord>()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        loadAll()
    }

    fun addGroup(g: BirdGroup) { groups.add(g); saveAll() }
    fun deleteGroup(id: String) {
        groups.removeAll { it.id == id }
        vaccinations.removeAll { it.groupId == id }
        medications.removeAll { it.groupId == id }
        symptoms.removeAll { it.groupId == id }
        saveAll()
    }
    fun updateGroupLastVax(groupId: String, date: String) {
        val idx = groups.indexOfFirst { it.id == groupId }
        if (idx >= 0) { groups[idx] = groups[idx].copy(lastVaccination = date); saveAll() }
    }
    fun addVaccination(v: VaccinationRecord) { vaccinations.add(v); updateGroupLastVax(v.groupId, v.date); saveAll() }
    fun updateVaccination(v: VaccinationRecord) {
        val idx = vaccinations.indexOfFirst { it.id == v.id }
        if (idx >= 0) vaccinations[idx] = v; saveAll()
    }
    fun addMedication(m: MedicationCourse) { medications.add(m); saveAll() }
    fun updateMedication(m: MedicationCourse) {
        val idx = medications.indexOfFirst { it.id == m.id }
        if (idx >= 0) medications[idx] = m; saveAll()
    }
    fun addSymptom(s: SymptomRecord) { symptoms.add(s); saveAll() }

    private fun saveAll() {
        val editor = prefs.edit()
        editor.putInt("gsize", groups.size)
        groups.forEachIndexed { i, g ->
            editor.putString("gid$i", g.id).putString("gn$i", g.name).putString("gt$i", g.birdType)
                .putString("gb$i", g.breed).putInt("gc$i", g.count).putString("ga$i", g.age)
                .putString("gh$i", g.housingType).putString("gnt$i", g.notes).putString("glv$i", g.lastVaccination)
        }
        editor.putInt("vsize", vaccinations.size)
        vaccinations.forEachIndexed { i, v ->
            editor.putString("vid$i", v.id).putString("vgid$i", v.groupId).putString("vn$i", v.vaccineName)
                .putString("vt$i", v.vaccineType).putString("vbn$i", v.batchNumber).putString("vd$i", v.date)
                .putString("vnd$i", v.nextDate).putString("vab$i", v.administeredBy).putString("vds$i", v.dosage)
                .putString("vr$i", v.route).putString("vnt$i", v.notes).putBoolean("vc$i", v.isCompleted)
                .putString("vrc$i", v.reactions)
        }
        editor.putInt("msize", medications.size)
        medications.forEachIndexed { i, m ->
            editor.putString("mid$i", m.id).putString("mgid$i", m.groupId).putString("mn$i", m.medicationName)
                .putString("mai$i", m.activeIngredient).putString("ms$i", m.startDate).putString("me$i", m.endDate)
                .putString("mf$i", m.frequency).putString("md$i", m.dosage).putString("mwp$i", m.withdrawalPeriod)
                .putBoolean("ma$i", m.isActive)
        }
        editor.putInt("ssize", symptoms.size)
        symptoms.forEachIndexed { i, s ->
            editor.putString("sid$i", s.id).putString("sgid$i", s.groupId).putString("sd$i", s.date)
                .putString("ss$i", s.symptoms).putString("ssv$i", s.severity).putString("sac$i", s.affectedCount)
                .putString("sdg$i", s.diagnosis).putString("st$i", s.treatment).putString("sn$i", s.notes)
        }
        editor.apply()
    }

    private fun loadAll() {
        groups.clear(); vaccinations.clear(); medications.clear(); symptoms.clear()
        val gs = prefs.getInt("gsize", 0)
        for (i in 0 until gs) groups.add(BirdGroup(
            prefs.getString("gid$i","")!!, prefs.getString("gn$i","")!!, prefs.getString("gt$i","")!!,
            prefs.getString("gb$i","")!!, prefs.getInt("gc$i",0), prefs.getString("ga$i","")!!,
            prefs.getString("gh$i","")!!, prefs.getString("gnt$i","")!!, prefs.getString("glv$i","")!!
        ))
        val vs = prefs.getInt("vsize", 0)
        for (i in 0 until vs) vaccinations.add(VaccinationRecord(
            prefs.getString("vid$i","")!!, prefs.getString("vgid$i","")!!, prefs.getString("vn$i","")!!,
            prefs.getString("vt$i","")!!, prefs.getString("vbn$i","")!!, prefs.getString("vd$i","")!!,
            prefs.getString("vnd$i","")!!, prefs.getString("vab$i","")!!, prefs.getString("vds$i","")!!,
            prefs.getString("vr$i","")!!, prefs.getString("vnt$i","")!!, prefs.getBoolean("vc$i",false),
            prefs.getString("vrc$i","")!!
        ))
        val ms = prefs.getInt("msize", 0)
        for (i in 0 until ms) medications.add(MedicationCourse(
            prefs.getString("mid$i","")!!, prefs.getString("mgid$i","")!!, prefs.getString("mn$i","")!!,
            prefs.getString("mai$i","")!!, prefs.getString("ms$i","")!!, prefs.getString("me$i","")!!,
            prefs.getString("mf$i","")!!, prefs.getString("md$i","")!!, prefs.getString("mwp$i","")!!,
            prefs.getBoolean("ma$i",true)
        ))
        val ss = prefs.getInt("ssize", 0)
        for (i in 0 until ss) symptoms.add(SymptomRecord(
            prefs.getString("sid$i","")!!, prefs.getString("sgid$i","")!!, prefs.getString("sd$i","")!!,
            prefs.getString("ss$i","")!!, prefs.getString("ssv$i","")!!, prefs.getString("sac$i","")!!,
            prefs.getString("sdg$i","")!!, prefs.getString("st$i","")!!, prefs.getString("sn$i","")!!
        ))
    }
}

// ==================== VACCINE INFORMATION DATABASE ====================
data class VaccineInfo(
    val name: String,
    val diseases: String,
    val birdTypes: String,
    val age: String,
    val method: String,
    val booster: String,
    val description: String
)

val vaccineDatabase = listOf(
    VaccineInfo(
        "Marek's Disease Vaccine",
        "Marek's Disease (Herpesvirus)",
        "Chickens",
        "Day-old chicks (subcutaneous) or in-ovo (18 days incubation)",
        "Subcutaneous injection or in-ovo",
        "Not required, lifelong immunity",
        "Marek's disease is a highly contagious viral neoplastic disease in chickens. The vaccine is administered to day-old chicks or in-ovo at 18 days of incubation. It prevents tumor formation and nervous system damage. Vaccinated birds can still carry and spread the virus but remain protected from clinical disease."
    ),
    VaccineInfo(
        "Newcastle Disease Vaccine (B1/LaSota)",
        "Newcastle Disease (Paramyxovirus)",
        "Chickens, Turkeys, Pigeons",
        "First dose: 1-7 days (B1 strain)\nBooster: 14-21 days (LaSota strain)",
        "Eye drop, nasal drop, drinking water, or spray",
        "Every 45-60 days in endemic areas",
        "Newcastle disease affects respiratory, nervous, and digestive systems. B1 strain is milder for initial vaccination, while LaSota provides stronger immunity for boosters. Regular vaccination is critical in endemic regions. Symptoms include respiratory distress, nervous signs, and drop in egg production."
    ),
    VaccineInfo(
        "Infectious Bronchitis Vaccine",
        "Infectious Bronchitis (Coronavirus)",
        "Chickens",
        "First dose: 1-7 days\nBooster: Every 45-60 days",
        "Spray, drinking water, or eye drop",
        "Every 45-60 days",
        "Infectious bronchitis causes respiratory disease, decreased egg production, and egg quality issues. Multiple serotypes exist requiring appropriate vaccine selection. The vaccine provides local immunity in the respiratory tract."
    ),
    VaccineInfo(
        "Infectious Bursal Disease (Gumboro) Vaccine",
        "Infectious Bursal Disease (Birnavirus)",
        "Chickens",
        "Mild strain: 7-14 days\nIntermediate: 14-21 days\nHot strain: 21-28 days",
        "Drinking water or eye drop",
        "Based on maternal antibody levels",
        "IBD targets the bursa of Fabricius causing immunosuppression. The timing of vaccination depends on maternal antibody levels. Three vaccine strains exist: mild, intermediate, and hot. Hot strains are used in high-challenge environments."
    ),
    VaccineInfo(
        "Fowl Pox Vaccine",
        "Fowl Pox (Avipoxvirus)",
        "Chickens, Turkeys",
        "6-10 weeks of age",
        "Wing web stab method",
        "Annually in endemic areas",
        "Fowl pox causes wart-like lesions on non-feathered areas and diphtheritic lesions in the mouth and upper respiratory tract. The vaccine is applied via wing web stab creating a small \"take\" that confirms successful vaccination. Mosquitoes transmit the disease."
    ),
    VaccineInfo(
        "Avian Encephalomyelitis Vaccine",
        "Avian Encephalomyelitis (Picornavirus)",
        "Chickens, Turkeys, Quail",
        "10-16 weeks (breeders only)",
        "Drinking water or wing web",
        "Not required after initial vaccination",
        "This vaccine prevents epidemic tremors in young chicks. It's typically administered to breeder flocks to provide maternal immunity to offspring. Vaccination is done 4 weeks before egg production begins. Clinical signs include ataxia and tremors."
    ),
    VaccineInfo(
        "Infectious Laryngotracheitis (ILT) Vaccine",
        "Infectious Laryngotracheitis (Herpesvirus)",
        "Chickens",
        "4-8 weeks minimum\nBooster: Before lay",
        "Eye drop or spray",
        "As needed based on exposure risk",
        "ILT causes severe respiratory distress with bloody mucus. The vaccine can cause mild reactions in young birds, hence minimum age requirement. Vaccinated birds become carriers. Only use in endemic areas."
    ),
    VaccineInfo(
        "Fowl Cholera Vaccine",
        "Fowl Cholera (Pasteurella multocida)",
        "Chickens, Turkeys, Ducks, Geese",
        "8-12 weeks\nBooster: Annually",
        "Subcutaneous or intramuscular injection",
        "Annually or semi-annually in high-risk areas",
        "Fowl cholera is a bacterial disease causing acute septicemia or chronic localized infections. Both live attenuated and inactivated bacterins are available. The vaccine provides serotype-specific protection."
    ),
    VaccineInfo(
        "Avian Influenza Vaccine",
        "Avian Influenza (Orthomyxovirus)",
        "Chickens, Turkeys, Ducks",
        "Depends on national program regulations",
        "Subcutaneous or intramuscular injection",
        "Based on epidemiological situation",
        "Avian influenza vaccination is regulated by national authorities. Killed oil-emulsion vaccines provide protection against specific subtypes. Vaccination programs must be combined with surveillance. DIVA strategies differentiate vaccinated from infected birds."
    ),
    VaccineInfo(
        "Duck Viral Hepatitis Vaccine",
        "Duck Hepatitis (Picornavirus)",
        "Ducks",
        "Day-old ducklings (for breeders)\nProgeny protected by maternal antibodies",
        "Subcutaneous injection or drinking water",
        "Before breeding season",
        "Duck viral hepatitis affects ducklings under 3 weeks with high mortality. Vaccination of breeder ducks provides maternal immunity to offspring. Live attenuated vaccines are commonly used. The disease causes rapid death with characteristic liver lesions."
    ),
    VaccineInfo(
        "Salmonella Vaccine",
        "Salmonellosis (S. Enteritidis, S. Typhimurium)",
        "Chickens, Turkeys",
        "Day-old (live vaccine)\n10-16 weeks (killed booster)",
        "Drinking water (live), injection (killed)",
        "Based on program requirements",
        "Salmonella vaccination reduces intestinal colonization and egg contamination. Both live and killed vaccines are used in layers and breeders. Part of national control programs in many countries. Reduces public health risk from foodborne salmonellosis."
    ),
    VaccineInfo(
        "Mycoplasma gallisepticum Vaccine",
        "Chronic Respiratory Disease (Mycoplasma)",
        "Chickens, Turkeys",
        "6-14 weeks",
        "Spray or eye drop (live F-strain)\nInjection (bacterin)",
        "As needed",
        "Mycoplasma causes chronic respiratory disease with reduced egg production. F-strain live vaccine provides mucosal immunity. Vaccination replaces field strains with vaccine strains. Bacterins provide systemic antibody response. Important for multi-age farms."
    )
)

// ==================== MAIN ACTIVITY ====================
/*class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repository.init(this)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF2E7D32), onPrimary = Color.White,
                primaryContainer = Color(0xFFC8E6C9), secondary = Color(0xFF0277BD),
                tertiary = Color(0xFFFF6F00), background = Color(0xFFF5F5F5),
                surface = Color.White, error = Color(0xFFD32F2F)
            )) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { App() }
            }
        }
    }
}*/

@Composable
fun NavigationGraph() {
    val nav = rememberNavController()
    Repository.init(LocalContext.current)
    NavHost(nav, "dashboard") {
        composable("dashboard") { DashboardScreen(nav) }
        composable("groups") { GroupsScreen(nav) }
        composable("add_group") { AddGroupScreen(nav) }
        composable("group_detail/{groupId}", arguments = listOf(navArgument("groupId") { type = NavType.StringType })) {
            GroupDetailScreen(nav, it.arguments?.getString("groupId")!!)
        }
        composable("vaccination/{groupId}", arguments = listOf(navArgument("groupId") { type = NavType.StringType })) {
            VaccinationScreen(nav, it.arguments?.getString("groupId")!!)
        }
        composable("medication/{groupId}", arguments = listOf(navArgument("groupId") { type = NavType.StringType })) {
            MedicationScreen(nav, it.arguments?.getString("groupId")!!)
        }
        composable("symptoms/{groupId}", arguments = listOf(navArgument("groupId") { type = NavType.StringType })) {
            SymptomsScreen(nav, it.arguments?.getString("groupId")!!)
        }
        composable("calendar") { CalendarScreen(nav) }
        composable("reminders") { RemindersScreen(nav) }
        composable("vaccine_info") { VaccineInfoScreen(nav) }
        composable("vaccine_detail/{vaccineIndex}", arguments = listOf(navArgument("vaccineIndex") { type = NavType.IntType })) {
            VaccineDetailScreen(nav, it.arguments?.getInt("vaccineIndex") ?: 0)
        }
    }
}

// ==================== VACCINE INFO SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineInfoScreen(nav: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poultry Vaccines Guide", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        }
    ) { p ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "📚 Complete Guide to Poultry Vaccination",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Vaccination is the cornerstone of poultry health management. " +
                                    "This guide covers essential vaccines for chickens, turkeys, ducks, and other domestic birds. " +
                                    "Each entry includes disease information, vaccination age, administration methods, and booster schedules. " +
                                    "Always consult your veterinarian before implementing a vaccination program.\n\n" +
                                    "Click on any vaccine for detailed information.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF33691E)
                        )
                    }
                }
            }

            vaccineDatabase.forEachIndexed { index, vaccine ->
                item {
                    Card(
                        Modifier.fillMaxWidth().clickable { nav.navigate("vaccine_detail/$index") },
                        RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFC8E6C9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Vaccines, null, tint = Color(0xFF2E7D32))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    vaccine.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    vaccine.diseases,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    "For: ${vaccine.birdTypes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineDetailScreen(nav: NavController, vaccineIndex: Int) {
    val vaccine = vaccineDatabase.getOrNull(vaccineIndex) ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaccine Details", color = Color.White) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        }
    ) { p ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            vaccine.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }

            item {
                InfoCard("🦠 Disease", vaccine.diseases)
            }

            item {
                InfoCard("🐔 Target Birds", vaccine.birdTypes)
            }

            item {
                Card(
                    Modifier.fillMaxWidth(),
                    RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        DetailRow("📅 Vaccination Age", vaccine.age)
                        Spacer(Modifier.height(12.dp))
                        DetailRow("💉 Administration Method", vaccine.method)
                        Spacer(Modifier.height(12.dp))
                        DetailRow("🔄 Booster Schedule", vaccine.booster)
                    }
                }
            }

            item {
                Card(
                    Modifier.fillMaxWidth(),
                    RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "📖 Detailed Description",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            vaccine.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF424242),
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun InfoCard(title: String, content: String) {
    Card(
        Modifier.fillMaxWidth(),
        RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(16.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(80.dp)
            )
            Text(
                content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242)
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E7D32))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

// ==================== DASHBOARD ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(nav: NavController) {
    val df = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bird Guard", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(true, {}, { Icon(Icons.Default.Dashboard, "Home") }, label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2E7D32)))
                NavigationBarItem(false, { nav.navigate("groups") }, { Icon(Icons.Default.Groups, "Groups") }, label = { Text("Groups") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2E7D32)))
                NavigationBarItem(false, { nav.navigate("vaccine_info") }, { Icon(Icons.Default.Vaccines, "Vaccines") }, label = { Text("Vaccines") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2E7D32)))
                NavigationBarItem(false, { nav.navigate("calendar") }, { Icon(Icons.Default.CalendarMonth, "Cal") }, label = { Text("Calendar") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2E7D32)))
                NavigationBarItem(false, { nav.navigate("reminders") }, { Icon(Icons.Default.Notifications, "Alerts") }, label = { Text("Reminders") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2E7D32)))
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { HeaderCard(df.format(Date())) }
            item { QuickActionsCard(nav) }
            item { StatsRow() }

            val due = Repository.vaccinations.filter { !it.isCompleted }
            if (due.isNotEmpty()) {
                item {
                    Text("🔔 Upcoming Vaccinations", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                items(due.take(5)) { v ->
                    val group = Repository.groups.find { it.id == v.groupId }
                    Card(
                        Modifier.fillMaxWidth().clickable { nav.navigate("group_detail/${v.groupId}") },
                        RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFFFA000))
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(v.vaccineName, fontWeight = FontWeight.Bold)
                                    Text("${group?.name ?: "Unknown"} • Next: ${v.nextDate}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            if (v.vaccineType.isNotEmpty()) {
                                Text("Type: ${v.vaccineType}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            val active = Repository.medications.filter { it.isActive }
            if (active.isNotEmpty()) {
                item {
                    Text("💊 Active Medications", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                items(active.take(5)) { m ->
                    Card(
                        Modifier.fillMaxWidth(),
                        RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(m.medicationName, fontWeight = FontWeight.Bold)
                                Text("${m.startDate} - ${m.endDate} • ${m.frequency}", style = MaterialTheme.typography.bodySmall)
                                if (m.dosage.isNotEmpty()) Text("Dosage: ${m.dosage}", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(m.isActive, { Repository.updateMedication(m.copy(isActive = it)) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderCard(today: String) {
    Card(
        Modifier.fillMaxWidth(),
        RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFC8E6C9)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Today, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Today", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(today, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
fun QuickActionsCard(nav: NavController) {
    Card(
        Modifier.fillMaxWidth(),
        RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Quick Actions", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                QBtn(Icons.Default.AddCircle, "New Group") { nav.navigate("add_group") }
                QBtn(Icons.Default.Vaccines, "Vaccinate") {
                    if (Repository.groups.isNotEmpty()) nav.navigate("vaccination/${Repository.groups[0].id}")
                }
                QBtn(Icons.Default.Medication, "Medicine") {
                    if (Repository.groups.isNotEmpty()) nav.navigate("medication/${Repository.groups[0].id}")
                }
                QBtn(Icons.Default.Healing, "Symptoms") {
                    if (Repository.groups.isNotEmpty()) nav.navigate("symptoms/${Repository.groups[0].id}")
                }
            }
        }
    }
}

@Composable
fun QBtn(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
        Box(Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.7f)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = Color(0xFF2E7D32), modifier = Modifier.size(26.dp))
        }
        Text(label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun StatsRow() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("${Repository.groups.size}", "Groups", Color(0xFF1B5E20), Color(0xFF4CAF50), Modifier.weight(1f))
        StatCard("${Repository.vaccinations.count { !it.isCompleted }}", "Due Vaccines", Color(0xFFFF6F00), Color(0xFFFFA000), Modifier.weight(1f))
    }
    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("${Repository.medications.count { it.isActive }}", "Active Meds", Color(0xFF01579B), Color(0xFF4FC3F7), Modifier.weight(1f))
        StatCard("${Repository.symptoms.size}", "Symptom Logs", Color(0xFF388E3C), Color(0xFF66BB6A), Modifier.weight(1f))
    }
}

@Composable
fun StatCard(v: String, l: String, c1: Color, c2: Color, m: Modifier) {
    Card(m, RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Box(Modifier.background(Brush.linearGradient(listOf(c1, c2))).padding(16.dp)) {
            Column {
                Text(v, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(l, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

// ==================== GROUPS ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(nav: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bird Groups", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        },
        floatingActionButton = {
            FloatingActionButton({ nav.navigate("add_group") }, containerColor = Color(0xFFFF6F00)) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { p ->
        if (Repository.groups.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Groups, null, Modifier.size(80.dp), tint = Color.Gray)
                    Spacer(Modifier.height(16.dp))
                    Text("No bird groups yet", style = MaterialTheme.typography.headlineSmall)
                    Text("Tap + to create your first group", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(p).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Repository.groups) { g ->
                    Card(
                        Modifier.fillMaxWidth().clickable { nav.navigate("group_detail/${g.id}") },
                        RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFC8E6C9)), contentAlignment = Alignment.Center) {
                                    Text(g.name.first().uppercase(), fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(g.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${g.birdType} • ${g.breed} • ${g.count} birds", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    if (g.lastVaccination.isNotEmpty()) Text("💉 Last vaccinated: ${g.lastVaccination}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                TextButton({ nav.navigate("vaccination/${g.id}") }) { Icon(Icons.Default.Vaccines, null, Modifier.size(18.dp)); Text("Vaccinate") }
                                TextButton({ nav.navigate("medication/${g.id}") }) { Icon(Icons.Default.Medication, null, Modifier.size(18.dp)); Text("Medicate") }
                                TextButton({ nav.navigate("symptoms/${g.id}") }) { Icon(Icons.Default.Healing, null, Modifier.size(18.dp)); Text("Symptoms") }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== ADD GROUP ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupScreen(nav: NavController) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var housing by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var err by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Bird Group", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Group Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF2E7D32)) }
            item { OutlinedTextField(name, { name = it; err = false }, label = { Text("Group Name *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(type, { type = it }, label = { Text("Bird Type") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), placeholder = { Text("Chickens") })
                    OutlinedTextField(breed, { breed = it }, label = { Text("Breed") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), placeholder = { Text("Leghorn") })
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(count, { count = it; err = false }, label = { Text("Count *") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(age, { age = it }, label = { Text("Age") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), placeholder = { Text("12 weeks") })
                }
            }
            item { OutlinedTextField(housing, { housing = it }, label = { Text("Housing Type") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Free range, barn, cage") }) }
            item { OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(12.dp)) }
            if (err) item { Text("Please fill required fields (*)", color = Color.Red, style = MaterialTheme.typography.bodySmall) }
            item {
                Button({
                    if (name.isNotBlank() && count.isNotBlank()) {
                        Repository.addGroup(BirdGroup(
                            name = name.trim(), birdType = type.trim(), breed = breed.trim(),
                            count = count.trim().toIntOrNull() ?: 0, age = age.trim(),
                            housingType = housing.trim(), notes = notes.trim()
                        ))
                        nav.navigateUp()
                    } else err = true
                }, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text("Create Group", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== GROUP DETAIL ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(nav: NavController, groupId: String) {
    val g = Repository.groups.find { it.id == groupId } ?: return
    val vaccs = Repository.vaccinations.filter { it.groupId == groupId }.sortedByDescending { it.date }
    val meds = Repository.medications.filter { it.groupId == groupId }
    val symps = Repository.symptoms.filter { it.groupId == groupId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(g.name, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                actions = { IconButton({ Repository.deleteGroup(groupId); nav.navigateUp() }) { Icon(Icons.Default.Delete, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        Modifier.background(Brush.linearGradient(listOf(Color(0xFF1B5E20), Color(0xFF4CAF50)))).padding(20.dp)
                    ) {
                        Column {
                            Text("${g.birdType} ${g.breed}", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("${g.count} birds • ${g.age} • ${g.housingType}", color = Color.White.copy(alpha = 0.9f))
                            if (g.notes.isNotEmpty()) Text(g.notes, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                            if (g.lastVaccination.isNotEmpty()) Text("Last vaccinated: ${g.lastVaccination}", color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailBtn("Vaccine", Icons.Default.Vaccines, Modifier.weight(1f)) { nav.navigate("vaccination/$groupId") }
                    DetailBtn("Medicine", Icons.Default.Medication, Modifier.weight(1f)) { nav.navigate("medication/$groupId") }
                    DetailBtn("Symptoms", Icons.Default.Healing, Modifier.weight(1f)) { nav.navigate("symptoms/$groupId") }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("💉 Vaccination Journal (${vaccs.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Surface(shape = CircleShape, color = Color(0xFFC8E6C9)) {
                        Text("${vaccs.size}", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (vaccs.isEmpty()) {
                item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Text("No vaccination records yet", Modifier.padding(16.dp), textAlign = TextAlign.Center, color = Color.Gray) } }
            } else {
                items(vaccs) { v ->
                    Card(
                        Modifier.fillMaxWidth(),
                        RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(if (v.isCompleted) Color(0xFF4CAF50) else Color(0xFFFFA000)))
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(v.vaccineName, fontWeight = FontWeight.Bold)
                                    if (v.vaccineType.isNotEmpty()) Text("Type: ${v.vaccineType}", style = MaterialTheme.typography.bodySmall)
                                    Row {
                                        Text("Date: ${v.date}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        Spacer(Modifier.width(16.dp))
                                        Text("Next: ${v.nextDate}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFA000))
                                    }
                                    if (v.batchNumber.isNotEmpty()) Text("Batch: ${v.batchNumber}", style = MaterialTheme.typography.labelSmall)
                                    if (v.administeredBy.isNotEmpty()) Text("Administered by: ${v.administeredBy}", style = MaterialTheme.typography.labelSmall)
                                    if (v.route.isNotEmpty()) Text("Route: ${v.route} • Dosage: ${v.dosage}", style = MaterialTheme.typography.labelSmall)
                                    if (v.reactions.isNotEmpty()) Text("Reactions: ${v.reactions}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                                    if (v.notes.isNotEmpty()) Text("Notes: ${v.notes}", style = MaterialTheme.typography.bodySmall)
                                }
                                Checkbox(v.isCompleted, { Repository.updateVaccination(v.copy(isCompleted = it)) },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50)))
                            }
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("💊 Medications (${meds.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Surface(shape = CircleShape, color = Color(0xFFE3F2FD)) {
                        Text("${meds.size}", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFF0277BD), fontWeight = FontWeight.Bold)
                    }
                }
            }
            items(meds) { m ->
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(m.medicationName, fontWeight = FontWeight.Bold)
                            if (m.activeIngredient.isNotEmpty()) Text("Active: ${m.activeIngredient}", style = MaterialTheme.typography.bodySmall)
                            Text("${m.startDate} - ${m.endDate}", style = MaterialTheme.typography.bodySmall)
                            Text(m.frequency, style = MaterialTheme.typography.bodySmall)
                            if (m.withdrawalPeriod.isNotEmpty()) Text("Withdrawal: ${m.withdrawalPeriod}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD32F2F))
                        }
                        Switch(m.isActive, { Repository.updateMedication(m.copy(isActive = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0277BD)))
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🩺 Symptom History (${symps.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Surface(shape = CircleShape, color = Color(0xFFFFEBEE)) {
                        Text("${symps.size}", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }
                }
            }
            items(symps) { s ->
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(s.date, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            Surface(shape = RoundedCornerShape(8.dp), color = when(s.severity) {
                                "High" -> Color(0xFFFFEBEE); "Medium" -> Color(0xFFFFF3E0); else -> Color(0xFFE8F5E9)
                            }) {
                                Text(s.severity, Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = when(s.severity) {
                                        "High" -> Color(0xFFD32F2F); "Medium" -> Color(0xFFFFA000); else -> Color(0xFF4CAF50)
                                    }, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(s.symptoms, fontWeight = FontWeight.Medium)
                        if (s.diagnosis.isNotEmpty()) Text("Diagnosis: ${s.diagnosis}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                        if (s.treatment.isNotEmpty()) Text("Treatment: ${s.treatment}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun DetailBtn(text: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier.clickable(onClick = onClick),
        RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
            Text(text, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

// ==================== ADD VACCINATION ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen(nav: NavController, groupId: String) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var batch by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var admin by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var route by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var reactions by remember { mutableStateOf("") }
    var err by remember { mutableStateOf(false) }

    val vaccineTypes = listOf("Live attenuated", "Inactivated/Killed", "Recombinant", "Toxoid", "Other")
    val routes = listOf("Subcutaneous", "Intramuscular", "Eye drop", "Nasal drop", "Drinking water", "Spray", "Wing web")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Vaccination", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Vaccination Record", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF2E7D32)) }
            item { OutlinedTextField(name, { name = it; err = false }, label = { Text("Vaccine Name *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) }

            item {
                Text("Vaccine Type", style = MaterialTheme.typography.labelMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    vaccineTypes.forEach { vt ->
                        FilterChip(
                            selected = type == vt,
                            onClick = { type = vt },
                            label = { Text(vt, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(batch, { batch = it }, label = { Text("Batch No.") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(dosage, { dosage = it }, label = { Text("Dosage") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), placeholder = { Text("0.5ml") })
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(date, { date = it; err = false }, label = { Text("Date * (DD.MM.YYYY)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(next, { next = it }, label = { Text("Next Date") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                }
            }

            item {
                Text("Route of Administration", style = MaterialTheme.typography.labelMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    routes.take(4).forEach { r ->
                        FilterChip(
                            selected = route == r,
                            onClick = { route = r },
                            label = { Text(r, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    routes.drop(4).forEach { r ->
                        FilterChip(
                            selected = route == r,
                            onClick = { route = r },
                            label = { Text(r, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item { OutlinedTextField(admin, { admin = it }, label = { Text("Administered By") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) }
            item { OutlinedTextField(reactions, { reactions = it }, label = { Text("Reactions Observed") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("None") }) }
            item { OutlinedTextField(notes, { notes = it }, label = { Text("Additional Notes") }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(12.dp)) }

            if (err) item { Text("Please fill required fields (*)", color = Color.Red, style = MaterialTheme.typography.bodySmall) }

            item {
                Button({
                    if (name.isNotBlank() && date.isNotBlank()) {
                        Repository.addVaccination(VaccinationRecord(
                            groupId = groupId, vaccineName = name.trim(), vaccineType = type,
                            batchNumber = batch.trim(), date = date.trim(),
                            nextDate = next.trim().ifBlank { date.trim() },
                            administeredBy = admin.trim(), dosage = dosage.trim(),
                            route = route, notes = notes.trim(), isCompleted = true,
                            reactions = reactions.trim()
                        ))
                        nav.navigateUp()
                    } else err = true
                }, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text("Save Vaccination Record", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== ADD MEDICATION ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(nav: NavController, groupId: String) {
    var name by remember { mutableStateOf("") }
    var ingredient by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var freq by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var withdrawal by remember { mutableStateOf("") }
    var err by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medication", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Medication Course", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF0277BD)) }
            item { OutlinedTextField(name, { name = it; err = false }, label = { Text("Medication Name *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) }
            item { OutlinedTextField(ingredient, { ingredient = it }, label = { Text("Active Ingredient") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(start, { start = it; err = false }, label = { Text("Start * (DD.MM.YYYY)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(end, { end = it; err = false }, label = { Text("End * (DD.MM.YYYY)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(freq, { freq = it }, label = { Text("Frequency") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), placeholder = { Text("2x daily") })
                    OutlinedTextField(dosage, { dosage = it }, label = { Text("Dosage") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), placeholder = { Text("5mg/kg") })
                }
            }
            item { OutlinedTextField(withdrawal, { withdrawal = it }, label = { Text("Withdrawal Period") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("7 days for eggs") }) }
            if (err) item { Text("Please fill required fields (*)", color = Color.Red, style = MaterialTheme.typography.bodySmall) }
            item {
                Button({
                    if (name.isNotBlank() && start.isNotBlank() && end.isNotBlank()) {
                        Repository.addMedication(MedicationCourse(
                            groupId = groupId, medicationName = name.trim(), activeIngredient = ingredient.trim(),
                            startDate = start.trim(), endDate = end.trim(),
                            frequency = freq.trim().ifBlank { "Daily" }, dosage = dosage.trim(),
                            withdrawalPeriod = withdrawal.trim(), isActive = true
                        ))
                        nav.navigateUp()
                    } else err = true
                }, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD))) {
                    Text("Save Medication Course", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== ADD SYMPTOMS ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(nav: NavController, groupId: String) {
    var desc by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var sev by remember { mutableStateOf("Medium") }
    var count by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var err by remember { mutableStateOf(false) }
    val df = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Symptoms", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Symptom Record - ${df.format(Date())}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFFD32F2F)) }
            item {
                Text("Severity Level", style = MaterialTheme.typography.labelMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Low", "Medium", "High").forEach { level ->
                        FilterChip(
                            selected = sev == level,
                            onClick = { sev = level },
                            label = { Text(level) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (level) {
                                    "High" -> Color(0xFFD32F2F).copy(alpha = 0.2f)
                                    "Medium" -> Color(0xFFFFA000).copy(alpha = 0.2f)
                                    else -> Color(0xFF388E3C).copy(alpha = 0.2f)
                                }
                            )
                        )
                    }
                }
            }
            item { OutlinedTextField(count, { count = it }, label = { Text("Number Affected") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }
            item { OutlinedTextField(desc, { desc = it; err = false }, label = { Text("Symptoms Description *") }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp)) }
            item { OutlinedTextField(diagnosis, { diagnosis = it }, label = { Text("Preliminary Diagnosis") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) }
            item { OutlinedTextField(treatment, { treatment = it }, label = { Text("Treatment Given") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) }
            item { OutlinedTextField(notes, { notes = it }, label = { Text("Additional Notes") }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(12.dp)) }
            if (err) item { Text("Please describe symptoms", color = Color.Red, style = MaterialTheme.typography.bodySmall) }
            item {
                Button({
                    if (desc.isNotBlank()) {
                        Repository.addSymptom(SymptomRecord(
                            groupId = groupId, date = df.format(Date()), symptoms = desc.trim(),
                            severity = sev, affectedCount = count.trim(), diagnosis = diagnosis.trim(),
                            treatment = treatment.trim(), notes = notes.trim()
                        ))
                        nav.navigateUp()
                    } else err = true
                }, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) {
                    Text("Save Symptom Record", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== CALENDAR ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(nav: NavController) {
    val df = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Calendar", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Today", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(df.format(Date()), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
            item { Text("💉 Vaccination Schedule", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) }
            if (Repository.vaccinations.isEmpty()) {
                item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Text("No vaccinations recorded", Modifier.padding(16.dp), textAlign = TextAlign.Center) } }
            } else {
                items(Repository.vaccinations.sortedBy { it.nextDate }) { v ->
                    val group = Repository.groups.find { it.id == v.groupId }
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(Modifier.padding(12.dp)) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(if (v.isCompleted) Color(0xFF4CAF50) else Color(0xFFFFA000)))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(v.vaccineName, fontWeight = FontWeight.Bold)
                                Text("${group?.name ?: "?"} • ${v.nextDate}", style = MaterialTheme.typography.bodySmall)
                                if (v.vaccineType.isNotEmpty()) Text("Type: ${v.vaccineType}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
            item { Text("💊 Active Medications", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
            if (Repository.medications.none { it.isActive }) {
                item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Text("No active medications", Modifier.padding(16.dp), textAlign = TextAlign.Center) } }
            } else {
                items(Repository.medications.filter { it.isActive }) { m ->
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(m.medicationName, fontWeight = FontWeight.Bold)
                            Text("${m.startDate} → ${m.endDate} • ${m.frequency}", style = MaterialTheme.typography.bodySmall)
                            if (m.dosage.isNotEmpty()) Text("Dosage: ${m.dosage}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ==================== REMINDERS ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(nav: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders & Alerts", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton({ nav.navigateUp() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val due = Repository.vaccinations.filter { !it.isCompleted }
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, null, tint = Color(0xFFFFA000), modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Vaccination Due (${due.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        if (due.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            due.forEach { v ->
                                val group = Repository.groups.find { it.id == v.groupId }
                                Row(Modifier.padding(top = 4.dp)) {
                                    Text("• ", color = Color(0xFFFFA000))
                                    Text("${v.vaccineName} — ${group?.name ?: "?"} — Due: ${v.nextDate}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            Spacer(Modifier.height(8.dp))
                            Text("All vaccinations are up to date! ✓", color = Color(0xFF4CAF50))
                        }
                    }
                }
            }

            val active = Repository.medications.filter { it.isActive }
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Medication, null, tint = Color(0xFF0277BD), modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Active Medications (${active.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        if (active.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            active.forEach { m ->
                                Row(Modifier.padding(top = 4.dp)) {
                                    Text("• ", color = Color(0xFF0277BD))
                                    Text("${m.medicationName} until ${m.endDate} — ${m.frequency}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            Spacer(Modifier.height(8.dp))
                            Text("No active medications", color = Color.Gray)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}