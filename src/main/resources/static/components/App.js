export default {
	name: "App",
	template: `
		<el-container>
			<el-header height="80px">
				<div class="header-wrapper">
					<img src="da-vinci-logo.png" alt="Da Vinci Logo">
					<img src="healthlx-logo.png" alt="HealthLX Logo">
				</div>
			</el-header>
	
			<el-main>
				<router-view></router-view>
			</el-main>
		</el-container>
	`
};
