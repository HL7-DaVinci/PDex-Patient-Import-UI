const path = require("path");

module.exports = {
	devServer: {
		proxy: {
			"/api": {
				target: "https://localhost:8443"
			},
			"/fhir": {
				target: "https://localhost:8443"
			}
		}
	},
	lintOnSave: process.env.NODE_ENV !== "production",
	css: {
		loaderOptions: {
			less: {
				lessOptions: {
					modifyVars: {
						hack: `true; @import '${path.resolve("./src/assets/less/vant-theme/index.less")}';`
					}
				}
			}
		}
	}
};
