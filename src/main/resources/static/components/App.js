export default {
	name: "App",
	template: `
		<b-container fluid>
			<b-row>
				<b-navbar type="light" variant="light" sticky="true" class="navbar">
					<b-navbar-brand href="/">
						<b-img left src="assets/da-vinci-logo.png" class="d-inline-block align-top logo-img" alt="Da Vinci Logo">
					</b-navbar-brand>
					<b-navbar-brand href="/">
						<b-img right src="assets/healthlx-logo.png" class="d-inline-block align-top logo-img" alt="HealthLX Logo">
					</b-navbar-brand>
				</b-navbar>
			</b-row>
	
			<b-row>
				<b-col cols="12">
					<el-main>
						<router-view></router-view>
					</el-main>
				<b-col>
			</b-row>
		</b-container>
	`
};
