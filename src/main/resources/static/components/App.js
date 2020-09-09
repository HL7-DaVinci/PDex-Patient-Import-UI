export default {
	name: "App",
	template: `
		<b-container fluid>
			<b-row>
				<b-navbar type="light" variant="light" sticky="true" class="navbar" toggleable="lg">
						<b-navbar-brand href="/">
								<b-img left src="assets/da-vinci-logo.png" class="d-inline-block align-top logo-img" alt="Da Vinci Logo">
						</b-navbar-brand>

						<b-navbar-brand href="/">
								<b-img right src="assets/healthlx-logo.png" class="d-inline-block align-top logo-img" alt="HealthLX Logo">
						</b-navbar-brand>

						<b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
						<b-collapse id="nav-collapse" is-nav>
								<b-navbar-nav class="ml-auto">
												<b-nav-item href="/" right><b-icon-house-fill></b-icon-house-fill> Home</b-nav-item>
												<b-nav-item href="/logout" right><b-icon-box-arrow-right></b-icon-box-arrow-right> Logout</b-nav-item>
								</b-navbar-nav>
						</b-collapse>
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
